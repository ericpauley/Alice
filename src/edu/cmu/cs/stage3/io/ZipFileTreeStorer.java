/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.io;

/**
 * @author David Culyba
 */

public class ZipFileTreeStorer implements edu.cmu.cs.stage3.io.DirectoryTreeStorer {

    private static final int ENDSIG = 0x06054b50;
    private static final int HEADSIG = 0x02014b50;
    private static final int LOCHEADSIG = 0x04034b50;
    private static final int DATADESCSIG = 0x08074b50;
    private static final int SIG = 1;
    private static final int MADEBY = 2;
    private static final int VERNEEDED = 3;
    private static final int BITFLAG = 4;
    private static final int COMPMETH = 5;
    private static final int MODTIME = 6;
    private static final int MODDATE = 7;
    private static final int CRC32 = 8;
    private static final int COMPSIZE = 9;
    private static final int UNCOMPSIZE = 10;
    private static final int NAMELENGTH = 11;
    private static final int EXTRALENGTH = 12;
    private static final int COMLENGTH = 13;
    private static final int DISKSTART = 14;
    private static final int INTATTRIB = 15;
    private static final int EXTATTRIB = 16;
    private static final int OFFSET = 17;
    private static final int FILENAME = 18;
    private static final int EXTRAFIELD = 19;
    private static final int COMMENT = 20;
    private static final int compressionBitFlag = 0;
    private static final int compressionMethodValue = 8;
    private static final int BITFLAG_DEFAULT = 0;

    private byte[] endHeader;
    private java.util.Vector centralDirectory;  //always sorted by location of local header in file from start of file to end
    private int centralDirectoryLocation;
    private String currentFile = null;
    private java.io.File rootFile;
    private int currentHeader = 0;
    private byte[] currentLocalFileData;
    private java.util.zip.CRC32 crc = new java.util.zip.CRC32();

    private java.util.Vector holes;
    private java.util.Vector toWrite = new java.util.Vector();
    private java.io.ByteArrayOutputStream m_currentlyOpenStream = null;
    private boolean outputStreamOpen = false;
    private java.io.RandomAccessFile m_randomAccessFile = null;
    private java.util.HashMap filenameToHeaderMap;
    private boolean newZip = false;
    private boolean allOpen = false;
    private boolean shouldCompressCurrent = true;
    private int lastEntry = -1;
    private int timeStamp = -1;
    private String currentDirectory;


    private class CentralDirectoryHeader{

        public int position;
        public int size;
        boolean shouldDelete = false;
        public LocalHeader localHeaderReference;
        public boolean newEntry = false;
        public boolean shouldCompress = true;
        public byte[] data = null;

        public int sig = HEADSIG;
        public int versionMadeBy  = 20;
        public int versionNeededToExtract = 20;
        public int bitFlag = BITFLAG_DEFAULT;
        public int compressionMethod = 8;
        public int lastModTime = 0;
        //public int lastModDate = 0;
        public int crc32 = 0;
        public int compressedSize = -1;
        public int uncompressedSize = -1;
        public int fileNameLength = 0;
        public int extraFieldLength = 0;
        public int fileCommentLength = 0;
        public int diskNumberStart = 0;
        public int internalFileAttributes = 0;
        public int externalFileAttributes = 0;
        public int relativeOffset = 0;
        public String fileName = "";
        public String extraField = "";
        public String comment = "";

        public CentralDirectoryHeader(){
        }

        public CentralDirectoryHeader(int offset, int pos, byte[] buf) throws java.lang.IllegalArgumentException{
            position = pos+offset;
            if (buf != null && buf.length > (42+offset)){
                sig = (int)getValue(buf, offset, 4);
                if (sig != HEADSIG){
                    throw new java.lang.IllegalArgumentException();
                }
                versionMadeBy = (int)getValue(buf, offset+4, 2);
                versionNeededToExtract = (int)getValue(buf, offset+6, 2);
                bitFlag = (int)getValue(buf, offset+8, 2);
                compressionMethod = (int)getValue(buf, offset+10, 2);
                lastModTime = (int)getValue(buf, offset+12, 4);
                //lastModDate = (int)getValue(buf, offset+14, 2);
                crc32 = (int)getValue(buf, offset+16, 4);
                compressedSize = (int)getValue(buf, offset+20, 4);
                uncompressedSize = (int)getValue(buf, offset+24, 4);
                fileNameLength = (int)getValue(buf, offset+28, 2);
                extraFieldLength = (int)getValue(buf, offset+30, 2);
                fileCommentLength = (int)getValue(buf, offset+32, 2);
                diskNumberStart = (int)getValue(buf, offset+34, 2);
                internalFileAttributes = (int)getValue(buf, offset+36, 2);
                externalFileAttributes = (int)getValue(buf, offset+38, 4);
                relativeOffset = (int)getValue(buf, offset+42, 4);
                fileName = getString(buf, offset+46, fileNameLength);
                extraField = getString(buf, offset+46+fileNameLength, extraFieldLength);
                comment = getString(buf, offset+46+fileNameLength+extraFieldLength, fileCommentLength);

                size = fileNameLength+extraFieldLength+fileCommentLength+46;
            }
            else{
                throw new java.lang.IllegalArgumentException();
            }
            if (compressionMethod == 0){
                shouldCompress = false;
            }
            else{
                shouldCompress = true;
            }
        }

        public void setCompression(boolean compressBool){
            shouldCompress = compressBool;
            if (shouldCompress){
                bitFlag = compressionBitFlag;
                compressionMethod = compressionMethodValue;
                if (localHeaderReference != null){
                    localHeaderReference.bitFlag = bitFlag;
                    localHeaderReference.compressionMethod = compressionMethod;
                }
            }
            else{
                bitFlag = BITFLAG_DEFAULT;
                compressionMethod = 0;
                if (localHeaderReference != null){
                    localHeaderReference.bitFlag = BITFLAG_DEFAULT;
                    localHeaderReference.compressionMethod = 0;
                }
            }
        }

        public int getLocalHeaderSpace(){
            if ((bitFlag & 8) > 0 && (localHeaderReference != null && localHeaderReference.hasDataDescriptor)){
//                System.out.println("has a descriptor");
                return 30+fileNameLength+extraFieldLength+compressedSize+16;
            }
            else{
//                System.out.println("no descriptor");
                return 30+fileNameLength+extraFieldLength+compressedSize;
            }
        }

        public int getThisHeaderSpace(){
            return 46+fileNameLength+extraFieldLength+fileCommentLength;
        }

        public void writeData(int toWrite) throws java.io.IOException{
            switch (toWrite){
                case SIG : writeInt(position+0,sig,4); break;
                case MADEBY : writeInt(position+4,versionMadeBy,2); break;
                case VERNEEDED : writeInt(position+6,versionNeededToExtract,2); break;
                case BITFLAG : writeInt(position+8,bitFlag,2); break;
                case COMPMETH : writeInt(position+10,compressionMethod,2); break;
                case MODTIME : writeInt(position+12,lastModTime,4); break;
                    //  case MODDATE : writeInt(position+14,lastModDate,2); break;
                case CRC32 : writeInt(position+16,crc32,4); break;
                case COMPSIZE : writeInt(position+20,compressedSize,4); break;
                case UNCOMPSIZE : writeInt(position+24,uncompressedSize,4); break;
                case NAMELENGTH : writeInt(position+28,fileNameLength,2); break;
                case EXTRALENGTH : writeInt(position+30,extraFieldLength,2); break;
                case COMLENGTH : writeInt(position+32,fileCommentLength,2); break;
                case DISKSTART : writeInt(position+34,diskNumberStart,2); break;
                case INTATTRIB : writeInt(position+36,internalFileAttributes,2); break;
                case EXTATTRIB : writeInt(position+38,externalFileAttributes,4); break;
                case OFFSET : writeInt(position+42,relativeOffset,4); break;
                case FILENAME : writeString(position+46,fileName); break;
                case EXTRAFIELD : writeString(position+46+fileName.length(),extraField); break;
                case COMMENT : writeString(position+46+fileName.length()+extraField.length(),comment); break;
                default : break;
            }
        }

        public void writeAll() throws java.io.IOException{
            for (int i=1; i<=20; i++){
                writeData(i);
            }
        }

        public void setShouldDelete(boolean toSetTo){
            shouldDelete = toSetTo;
            //System.out.println("set "+fileName+" shouldDelete to "+shouldDelete);
        }

        public void setFileName(String name){
            fileName = new String(name);
            fileNameLength = fileName.length();
            if (localHeaderReference != null){
                localHeaderReference.fileName = new String(fileName);
                localHeaderReference.fileNameLength = fileName.length();
            }
        }

        public String toString(){
            String toReturn = "";
            toReturn += "position in file: "+String.valueOf(position)+"\n";
            toReturn += "size: "+String.valueOf(size)+"\n";
            toReturn += "sig: "+String.valueOf(sig)+"\n";
            toReturn += "version made: "+String.valueOf(versionMadeBy)+"\n";
            toReturn += "version needed: "+String.valueOf(versionNeededToExtract)+"\n";
            toReturn += "bit flag: "+String.valueOf(bitFlag)+"\n";
            toReturn += "compression method: "+String.valueOf(compressionMethod)+"\n";
            toReturn += "las mod time: "+String.valueOf(lastModTime)+"\n";
            //toReturn += "last mod date: "+String.valueOf(lastModDate)+"\n";
            toReturn += " crc-32: "+String.valueOf(crc32)+"\n";
            toReturn += "compressed size: "+String.valueOf(compressedSize)+"\n";
            toReturn += "uncompressed size: "+String.valueOf(uncompressedSize)+"\n";
            toReturn += "file name length: "+String.valueOf(fileNameLength)+"\n";
            toReturn += "extra field length: "+String.valueOf(extraFieldLength)+"\n";
            toReturn += "comment length: "+String.valueOf(fileCommentLength)+"\n";
            toReturn += "disk number start: "+String.valueOf(diskNumberStart)+"\n";;
            toReturn += "internal attrib: "+String.valueOf(internalFileAttributes)+"\n";
            toReturn += "external attrib: "+String.valueOf(externalFileAttributes)+"\n";
            toReturn += "relative offset for local header: "+String.valueOf(relativeOffset)+"\n";
            toReturn += ("file name: "+fileName+", spans from "+String.valueOf(position)+" to "+String.valueOf((position+getThisHeaderSpace()))+"\n");
            toReturn += "extra field: "+extraField+"\n";
            toReturn += "comment: "+comment;

            return toReturn;
        }

    }

    private class LocalHeader{

        public int position;
        public int size;
        public CentralDirectoryHeader centralDirectoryReference;

        public int sig = LOCHEADSIG;
        public int versionNeededToExtract = 20;
        public int bitFlag = BITFLAG_DEFAULT;
        public int compressionMethod = 8;
        public int lastModTime = 0;
        // public int lastModDate = 0;
        public int crc32 = 0;
        public int compressedSize = -1;
        public int uncompressedSize = -1;
        public int fileNameLength = 0;
        public int extraFieldLength = 0;
        public String fileName = "";
        public String extraField = "";
        public boolean hasDataDescriptor = false;

        public LocalHeader(){
        }

        public LocalHeader(int pos, CentralDirectoryHeader cd, byte[] buf) throws java.lang.IllegalArgumentException{
            centralDirectoryReference = cd;
            position = pos;
            if (buf != null && buf.length > 30){
                sig = (int)getValue(buf, 0, 4);
                if (sig != LOCHEADSIG){
                //	System.out.println("SIG is wrong in "+cd.fileName+" \n"+Integer.toBinaryString(sig)+"\n"+Integer.toBinaryString(LOCHEADSIG));
                    throw new java.lang.IllegalArgumentException();
                }
                versionNeededToExtract = (int)getValue(buf, 4, 2);
                bitFlag = (int)getValue(buf, 6, 2);
                compressionMethod = (int)getValue(buf, 8, 2);
                lastModTime = (int)getValue(buf, 10, 4);
                //lastModDate = (int)getValue(buf, 12, 2);
                crc32 = (int)getValue(buf, 14, 4);
                compressedSize = (int)getValue(buf, 18, 4);
                uncompressedSize = (int)getValue(buf, 22, 4);
                fileNameLength = (int)getValue(buf, 26, 2);
                extraFieldLength = (int)getValue(buf, 28, 2);
                fileName = getString(buf, 30, fileNameLength);
                extraField = getString(buf, 30+fileNameLength, extraFieldLength);

                size = fileNameLength+extraFieldLength+30;

                hasDataDescriptor = false;
//				if (sig != LOCHEADSIG){
//					System.out.println(this+"\n");
//				 //   throw new java.lang.IllegalArgumentException();
//				}

            }
            else{
                throw new java.lang.IllegalArgumentException();
            }
        }

        public int getThisHeaderSpace(){
            return 30+fileNameLength+extraFieldLength;
        }

        public boolean needsDataDescriptor(){
            return ((bitFlag & 8) > 0);
        }

        public void writeData(int toWrite) throws java.io.IOException{
            switch (toWrite){
                case SIG : writeInt(position+0,sig,4); break;
                case VERNEEDED : writeInt(position+4,versionNeededToExtract,2); break;
                case BITFLAG : writeInt(position+6,bitFlag,2); break;
                case COMPMETH : writeInt(position+8,compressionMethod,2); break;
                case MODTIME : writeInt(position+10,lastModTime,4); break;
                    //  case MODDATE : writeInt(position+12,lastModDate,2); break;
                case CRC32 : writeInt(position+14,crc32,4); break;
                case COMPSIZE : writeInt(position+18,compressedSize,4); break;
                case UNCOMPSIZE : writeInt(position+22,uncompressedSize,4); break;
                case NAMELENGTH : writeInt(position+26,fileNameLength,2); break;
                case EXTRALENGTH : writeInt(position+28,extraFieldLength,2); break;
                case FILENAME : writeString(position+30,fileName); break;
                case EXTRAFIELD : writeString(position+30+fileName.length(),extraField); break;
                default : break;
            }
        }

        public void writeAll() throws java.io.IOException{
            for (int i=1; i<=20; i++){
                writeData(i);
            }
        }

        public void writeDataDescriptor() throws java.io.IOException{
            int startPosition = position+getThisHeaderSpace()+compressedSize;
//            System.out.println("writing data descriptor at "+startPosition+" for: \n"+this);
            writeInt(startPosition, DATADESCSIG, 4);
            writeInt(startPosition+4, crc32, 4);
            writeInt(startPosition+8, compressedSize, 4);
            writeInt(startPosition+12, uncompressedSize, 4);
        }

        public String toString(){
            String toReturn = "";
            toReturn += "position in file: "+String.valueOf(position)+"\n";
            toReturn += "size: "+String.valueOf(size)+"\n";
            toReturn += "sig: "+String.valueOf(sig)+"\n";
            toReturn += "version made: "+String.valueOf(versionNeededToExtract)+"\n";
            toReturn += "bit flag: "+String.valueOf(bitFlag)+"\n";
            toReturn += "compression method: "+String.valueOf(compressionMethod)+"\n";
            toReturn += "las mod time: "+String.valueOf(lastModTime)+"\n";
            // toReturn += "last mod date: "+String.valueOf(lastModDate)+"\n";
            toReturn += "crc-32: "+String.valueOf(crc32)+"\n";
            toReturn += "compressed size: "+String.valueOf(compressedSize)+"\n";
            toReturn += "uncompressed size: "+String.valueOf(uncompressedSize)+"\n";
            toReturn += "file name length: "+String.valueOf(fileNameLength)+"\n";
            toReturn += "extra field length: "+String.valueOf(extraFieldLength)+"\n";
            toReturn += "local header file name: "+fileName+" spans from "+String.valueOf(position)+" to "+String.valueOf(position+getThisHeaderSpace()+compressedSize)+"\n";
            toReturn += "extra field: "+extraField+"\n";
            if (hasDataDescriptor){
                toReturn += "There IS a data descriptor\n";
            }
            else{
                toReturn += "There ISN'T a data descriptor\n";
            }


            return toReturn;
        }
    }

    private class LocalizedVacuity{
        public int pos;
        public int size;

        public LocalizedVacuity(int newpos, int newsize){
            pos = newpos;
            size = newsize;
        }

        public String toString(){
            return "position: "+String.valueOf(pos)+", size: "+String.valueOf(size);
        }
    }

    private class HoleComparator implements java.util.Comparator{
        public int compare(Object a, Object b){
            LocalizedVacuity holeA = (LocalizedVacuity)a;
            LocalizedVacuity holeB = (LocalizedVacuity)b;
            return (holeA.size - holeB.size);
        }
    }

    private class HeaderSizeComparator implements java.util.Comparator{
        public int compare(Object a, Object b){
            CentralDirectoryHeader headerA = (CentralDirectoryHeader)a;
            CentralDirectoryHeader headerB = (CentralDirectoryHeader)b;
            int headerASize = headerA.getLocalHeaderSpace();
            int headerBSize = headerB.getLocalHeaderSpace();
            return (headerASize - headerBSize);
        }
    }

    private class HeaderLocationComparator implements java.util.Comparator{
        public int compare(Object a, Object b){
            CentralDirectoryHeader headerA = (CentralDirectoryHeader)a;
            CentralDirectoryHeader headerB = (CentralDirectoryHeader)b;
            return (headerA.relativeOffset - headerB.relativeOffset);
        }
    }

    private HoleComparator holeComparator = new HoleComparator();
    private HeaderSizeComparator headerSizeComparator = new HeaderSizeComparator();
    private HeaderLocationComparator headerLocationComparator = new HeaderLocationComparator();

    private static long getCurrentDosTime() {
        java.util.Calendar d = java.util.Calendar.getInstance();
        int year = d.get(java.util.Calendar.YEAR);
        if (year < 1980) {
            return (1 << 21) | (1 << 16);
        }
        return (year - 1980) << 25 | (d.get(java.util.Calendar.MONTH) + 1) << 21 |
                d.get(java.util.Calendar.DAY_OF_MONTH) << 16 | d.get(java.util.Calendar.HOUR_OF_DAY) << 11 | d.get(java.util.Calendar.MINUTE) << 5 |
                d.get(java.util.Calendar.SECOND) >> 1;
    }

    private void writeInt(int pos, int value, int size) throws java.io.IOException{
        int mask = 0xFF;
        try{
            m_randomAccessFile.seek((long)(pos));
            for (int  i=0; i<size; i++){
                m_randomAccessFile.writeByte((mask & value));
                value >>= 8;
            }
        }
        catch (java.io.IOException e){
            throw e;
        }
    }

    private void writeString(int pos, String toWrite) throws java.io.IOException{
        try{
            m_randomAccessFile.seek((long)(pos));
            m_randomAccessFile.write(toWrite.getBytes());
        }
        catch (java.io.IOException e){
            throw e;
        }
    }

    private static String getCanonicalPathname( String pathname ) {
        pathname = pathname.replace( '\\', '/' );
        // remove double separators
        int index;
        while( (index = pathname.indexOf( "//" )) != -1 ) {
            pathname = pathname.substring( 0, index + 1 ) + pathname.substring( index + 2 );
        }

        if( pathname.charAt( 0 ) == '/' ) {
            pathname = pathname.substring( 1 );
        }
        if (pathname.endsWith( "/" ) ){
            pathname  = pathname.substring( 0, pathname.length()-2 );
        }
        return pathname;
    }

    public void setCurrentDirectory( String pathname ) throws IllegalArgumentException {
        if( pathname == null ) {
            pathname = "";
        }
        else if( pathname.length() > 0 ) {
            pathname = pathname.replace( '\\', '/' );

            // remove double separators
            int index;
            while( (index = pathname.indexOf( "//" )) != -1 ) {
                pathname = pathname.substring( 0, index + 1 ) + pathname.substring( index + 2 );
            }

            if( pathname.charAt( 0 ) == '/' ) {
                pathname = pathname.substring( 1 );
            }
            else {
                pathname = currentDirectory + pathname;
            }

            if( ! pathname.endsWith( "/" ) ) {
                pathname = pathname + "/";
            }
            if( !pathname.startsWith( "/" ) ) {
                pathname = "/"+pathname;
            }
        }

        currentDirectory = pathname;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    protected boolean isCompressed(){
        return true;
    }

    /**
     * Opens the zip file and marks all current files to be kept
     * @param pathname
     * @throws IllegalArgumentException
     * @throws java.io.IOException
     */

    public void openForUpdate( Object pathname ) throws IllegalArgumentException, java.io.IOException {
        open(pathname);
        for (int i=0; i<centralDirectory.size(); i++){
            ((CentralDirectoryHeader)centralDirectory.get(i)).shouldDelete = false;
        }
    }

    public void open( Object pathname ) throws IllegalArgumentException, java.io.IOException {
        if (allOpen){
            this.close();
        }
        if( pathname instanceof String ) {
            m_randomAccessFile = new java.io.RandomAccessFile( (String)pathname, "rw" );
            rootFile = new java.io.File((String)pathname);
        } else if( pathname instanceof java.io.File ) {
            m_randomAccessFile = new java.io.RandomAccessFile( (java.io.File)pathname, "rw" );
            rootFile = (java.io.File)pathname;
        } else if ( pathname instanceof java.io.OutputStream ){
            throw new IllegalArgumentException( "pathname must be an instance of String or java.io.File" );
        }else if( pathname == null ) {
            throw new IllegalArgumentException( "pathname is null" );
        } else {
            throw new IllegalArgumentException( "pathname must be an instance of String or java.io.File" );
        }
        if (m_randomAccessFile.length() <= 0){
            newZip = true;
        }
        currentDirectory = "";
        m_currentlyOpenStream = null;
        if (!newZip){
            try{
                setAtEndSig();
            }
            catch (java.io.IOException e){
                newZip = true;
            }
        }
        timeStamp = (int)getCurrentDosTime();
        initCentralDirectory();

        setEnd();
        allOpen = true;
    }

    /**
     * Finds the holes in the zip file based on the state of the <code>centralDirectory</code>.
     * Also sets the value of <code>lastEntry</code> to the end of the data portion of the zip file.
     */
    private void findHoles(){
        lastEntry = -1;
        if (centralDirectory != null){
            holes = new java.util.Vector();
            for(int i=0; i<centralDirectory.size()-1; i++){
                CentralDirectoryHeader current = (CentralDirectoryHeader)centralDirectory.get(i);
                CentralDirectoryHeader next = (CentralDirectoryHeader)centralDirectory.get(i+1);
                int currentSize = 30 + current.fileNameLength + current.extraFieldLength + current.compressedSize;
                int currentEnd = current.relativeOffset + currentSize;
                if (currentEnd < next.relativeOffset){
                    LocalizedVacuity hole = new LocalizedVacuity(currentEnd, (next.relativeOffset-currentEnd));
                    holes.add(hole);
                }
            }
            if (centralDirectory.size() == 0){
                lastEntry = 0;
            }
            else{
                CentralDirectoryHeader last = (CentralDirectoryHeader)centralDirectory.get(centralDirectory.size()-1);
                int lastSize = 30 + last.fileNameLength + last.extraFieldLength + last.compressedSize;
                int currentEnd = last.relativeOffset + lastSize;
                lastEntry = currentEnd;
                java.util.Collections.sort(holes, holeComparator);
            }
        }
    }

    private void fillHoles(){
        findHoles();
        for (int i=0; i<holes.size(); i++){
            LocalizedVacuity currentHole = (LocalizedVacuity)holes.get(i);
            byte[] bufOfZeroes = new byte[currentHole.size];
            try{
            	writeValue(bufOfZeroes, 0, currentHole.size, currentHole.pos);
            } catch (Exception e){ //TODO: handle filling holes
            }
        }
    }

    private void setEnd(){
        lastEntry = 0;
        if (centralDirectory != null && centralDirectory.size() > 0){
            CentralDirectoryHeader last = (CentralDirectoryHeader)centralDirectory.get(centralDirectory.size()-1);
            int lastSize = last.getLocalHeaderSpace();
            int currentEnd = last.relativeOffset + lastSize;
            lastEntry = currentEnd;
        }
    }

    private void investigateHole(LocalizedVacuity h) throws java.io.IOException {
        //System.out.println("hole: "+h);
        try{
            m_randomAccessFile.seek(h.pos);
            byte[] buf = new byte[h.size];
            m_randomAccessFile.readFully(buf);
            for (int i=0; i<buf.length; i++){
                System.out.print((char)buf[i]);
            }
        }
        catch(java.io.IOException e){
            throw e;
        }
    }


    /**
     *  Updates <code>header</code> to reflect the new <code>data</code> to be written.
     *  If the size of <code>data</code> is larger than the previous size, then the <code>data</code>
     *  is appended to the <code>header</code>. Then the <code>header</code> is placed in <code>toWrite</code> to be sorted
     *  out later and removed from the <code>centralDirectory</code>.
     */
    private void updateHeader(CentralDirectoryHeader header, byte[] data, boolean writeImmediately) throws java.io.IOException{
        try{
            int newSize = 0;
            int oldSize = header.compressedSize;
            int inflatedSize = 0;
            if (data != null){
                inflatedSize = data.length;
                crc.reset();
                crc.update(data);
                if (header.shouldCompress){
                    data = compressData(data);
                }
                newSize = data.length;
            }
            header.crc32 = (int)crc.getValue();
            header.compressedSize = newSize;
            header.uncompressedSize = inflatedSize;
            if (header.localHeaderReference != null){
                header.localHeaderReference.crc32 = (int)crc.getValue();
                header.localHeaderReference.compressedSize = newSize;
                header.localHeaderReference.uncompressedSize = inflatedSize;
            }
            if (newSize > oldSize && !writeImmediately){
 //               System.out.println("It's too big and we can delay the writing");
                header.setShouldDelete(true);
                header.data = data;
                toWrite.add(header);
                centralDirectory.remove(header);
            }
            else{
                header.data = null;
                if (writeImmediately){
                    //     System.out.println("writing local header "+header.fileName+" from "+header.relativeOffset+" to "+(header.relativeOffset +header.getLocalHeaderSpace()));
                    header.localHeaderReference.writeAll();
                }
                else{
                    //     System.out.println("I'm modifying "+header.fileName+" in place at "+header.relativeOffset+", old size: "+oldSize+", newSize: "+newSize);
                    header.localHeaderReference.writeData(CRC32);
                    header.localHeaderReference.writeData(COMPSIZE);
                    header.localHeaderReference.writeData(UNCOMPSIZE);
                    header.localHeaderReference.writeData(MODTIME);
                }
                writeValue(data, 0, newSize, header.relativeOffset+header.localHeaderReference.getThisHeaderSpace());
                if (header.localHeaderReference.needsDataDescriptor() && header.localHeaderReference.hasDataDescriptor){
                    header.localHeaderReference.writeDataDescriptor();
                }
            }

        }
        catch (java.io.IOException e){
            throw e;
        }
    }


    private byte[] compressData(byte[] data){
        java.util.zip.Deflater deflater = new java.util.zip.Deflater(java.util.zip.Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(data);
        int totalCompressed = 0;
        java.io.ByteArrayOutputStream compressedData = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[512];
        while (!deflater.needsInput()){
            int justCompressed = deflater.deflate(buf, 0, buf.length);
            if (justCompressed > 0) {
                compressedData.write(buf, 0, justCompressed);
            }
            totalCompressed += justCompressed;
        }
        deflater.finish();
        while (!deflater.finished()){
            int justCompressed = deflater.deflate(buf, 0, buf.length);
            if (justCompressed > 0) {
                compressedData.write(buf, 0, justCompressed);
            }
            totalCompressed += justCompressed;
        }
        return compressedData.toByteArray();
    }

    private int getUnsigned(byte toConvert){
        if (toConvert<0){
            return toConvert+256;
        }
        return toConvert;
    }

    private long getValue(byte[] buf, int offset, int size){
        long toReturn = 0;
        for (int  i=size-1; i>=0; i--){
            int toAdd = getUnsigned(buf[offset+i]);
            toReturn +=  toAdd;
            if (i!=0){
                toReturn <<= 8;
            }
        }
        return toReturn;
    }

    private void setValue(byte[] buf, int offset, int size, long value){
        long mask = 0xFF;
        long temp = value;
        for (int  i=0; i<size; i++){
            buf[offset+i] = (byte)(mask & temp);
            temp >>= 8;
        }
    }

    private void writeValue(byte[] buf, int offset, int size, int pos) throws java.io.IOException{
        if (buf != null){
            try{
                m_randomAccessFile.seek(pos);
                m_randomAccessFile.write(buf, offset, size);
            }
            catch(java.lang.Exception e){
				throw new java.io.IOException("An error occurred while writing to the zip file. The file may not have been saved properly.");
            }
        }
    }

    private String getString(byte[] buf, int offset, int size){
		//this seems a tad too wasteful. dennisc
		//for (int  i=0; i<size; i++){
		//	char toAdd = (char)getUnsigned(buf[offset+i]);
		//	toReturn +=  toAdd;
		//}
		//return toReturn;

		// this seems a tad too risky a change.  dennisc
    	//return new String( buf, offset, size );

		// this seems just right.  dennisc
		StringBuffer sb = new StringBuffer( size );
		for (int  i=0; i<size; i++){
			sb.append( (char)getUnsigned(buf[offset+i]) );
		}
		return sb.toString();
    }



    private void printFileHeaders(){
        for (int i=0; i<centralDirectory.size(); i++){
            System.out.println(centralDirectory.get(i));
        }
    }

    private void printEndHeader(){
        System.out.println("end header");
        System.out.println("Sig: "+getValue(endHeader, 0, 4));
        System.out.println("number of this disk: "+getValue(endHeader, 4, 2));
        System.out.println("number of the disk with the start of the cd: "+getValue(endHeader, 6, 2));
        System.out.println("total entries on this disk: "+getValue(endHeader, 8, 2));
        System.out.println("total entries: "+getValue(endHeader, 10, 2));
        System.out.println("size of cd: "+getValue(endHeader, 12, 4));
        System.out.println("offset of start of cd: "+getValue(endHeader, 16, 4));
        long commentLength = getValue(endHeader, 20, 2);
        System.out.println("comment length: "+commentLength);
    }

    private void initCentralDirectory() throws java.io.IOException{
        centralDirectory = new java.util.Vector();
        filenameToHeaderMap = new java.util.HashMap();
        if (newZip){
            return;
        }
        int size = (int)getValue(endHeader, 12, 4);
        long offset = getValue(endHeader, 16, 4);
        int currentHeaderOffset = 0;
        centralDirectoryLocation = (int)offset;
        byte[] centralArray = new byte[size];
        try{
            m_randomAccessFile.seek(offset);
            m_randomAccessFile.readFully(centralArray, 0, size);
        }
        catch (java.io.IOException e){
            newZip = true;
            return;
        }
        currentHeaderOffset = 0;
        int currentSize = 46;
        while (currentHeaderOffset < centralArray.length){
            if (currentHeaderOffset + 46 >= centralArray.length){
                currentHeaderOffset = centralArray.length;
                break;
            }
            CentralDirectoryHeader header = null;
            try{
                header = new CentralDirectoryHeader(currentHeaderOffset, centralDirectoryLocation, centralArray);
            }
            catch (java.lang.IllegalArgumentException e){
                centralDirectory = new java.util.Vector();
                filenameToHeaderMap = new java.util.HashMap();
                newZip = true;
                return;
            }
            currentHeaderOffset += header.size;
            header.setShouldDelete(true);
            centralDirectory.add(header);
            filenameToHeaderMap.put(header.fileName, header);
            try{
              //  System.out.println("trying to init local header from: \n"+header+"\n");
                header.localHeaderReference = initLocalHeader(header);
            }
            catch (java.io.IOException e){
                centralDirectory = new java.util.Vector();
                filenameToHeaderMap = new java.util.HashMap();
                newZip = true;
                return;
            }
            if (currentHeaderOffset +46 >= centralArray.length){
                currentHeaderOffset = centralArray.length;
                break;
            }
            if (getValue(centralArray, currentHeaderOffset, 4) != HEADSIG){
                currentHeaderOffset -= currentSize+1;
                while (currentHeaderOffset+3 < centralArray.length && getValue(centralArray, currentHeaderOffset, 4) != HEADSIG){
                    currentHeaderOffset++;
                }
            }
        }
        java.util.Collections.sort(centralDirectory, headerLocationComparator);
    }

    private LocalHeader getCurrentLocalHeader(){
        if (centralDirectory != null && currentHeader < centralDirectory.size()){
            return ((CentralDirectoryHeader)centralDirectory.get(currentHeader)).localHeaderReference;
        }
        else{
            return null;
        }
    }

    private CentralDirectoryHeader getCurrentCentralDirectoryHeader(){
        if (centralDirectory != null && currentHeader < centralDirectory.size()){
            return (CentralDirectoryHeader)centralDirectory.get(currentHeader);
        }
        else{
            return null;
        }
    }

    private LocalHeader initLocalHeader(CentralDirectoryHeader headerReference) throws java.io.IOException{
        int pos = headerReference.relativeOffset;
        int headerSize = headerReference.fileNameLength + headerReference.extraFieldLength + 30;
        byte[] currentLocalHeaderBytes = new byte[headerSize];
        LocalHeader headerToReturn = null;

        try{
            m_randomAccessFile.seek(pos);
            m_randomAccessFile.readFully(currentLocalHeaderBytes, 0, headerSize);
            headerToReturn = new LocalHeader(pos, headerReference, currentLocalHeaderBytes);
            m_randomAccessFile.seek(headerToReturn.getThisHeaderSpace()+headerToReturn.compressedSize);
            byte[] dataDescriptor = new byte[4];
            m_randomAccessFile.readFully(dataDescriptor, 0, 4);
            if (getValue(dataDescriptor, 0, 4) == DATADESCSIG){
                headerToReturn.hasDataDescriptor = true;
            }
        }
        catch (java.io.IOException e){
            throw e;
        }
        return headerToReturn;

    }

    private void printHex(byte[] buf, int offset, int length){
        for (int i=offset; i<length+offset; i++){
            byte myByte = buf[i];
            for (int j=0; j<2; j++){
                byte current = (byte)(myByte >> 4*(1-j));
                current = (byte)(current & 15);
                switch (current){
                    case 0 : System.out.print("0"); break;
                    case 1 : System.out.print("1"); break;
                    case 2 : System.out.print("2"); break;
                    case 3 : System.out.print("3"); break;
                    case 4 : System.out.print("4"); break;
                    case  5 : System.out.print("5"); break;
                    case  6 : System.out.print("6"); break;
                    case 7 : System.out.print("7"); break;
                    case 8 : System.out.print("8"); break;
                    case 9 : System.out.print("9"); break;
                    case 10 : System.out.print("a"); break;
                    case 11 : System.out.print("b"); break;
                    case  12 : System.out.print("c"); break;
                    case 13 : System.out.print("d"); break;
                    case 14 : System.out.print("e"); break;
                    case 15 : System.out.print("f"); break;
                };
            }
            System.out.print(" ");
        }
        System.out.println();
    }

    private void setAtEndSig() throws java.io.IOException{
        try{
            long len;
            long pos;
            len = pos = m_randomAccessFile.length();
            int endHeaderLength = 22;
            int maxSize = 0xFFFF;
            byte buf[];
            buf = new byte[endHeaderLength*2];
            java.util.Arrays.fill(buf, (byte)(0));
            while (len-pos < maxSize){
                for (int i=0; i<endHeaderLength; i++){
                    buf[i+endHeaderLength] = buf[i];
                }
                pos -= endHeaderLength;
                m_randomAccessFile.seek(pos);
                m_randomAccessFile.readFully(buf, 0, endHeaderLength);
                for (int i=0; i<(endHeaderLength*2)-3; i++){
                    if (getValue(buf, i, 4) == ENDSIG){
                        long endpos = pos+i;
                        int commentLength = (int)getValue(buf, i+20, 2);
                        if (endpos + commentLength + endHeaderLength == len){
                            endHeader = new byte[commentLength + endHeaderLength];
                            m_randomAccessFile.seek(endpos);
                            m_randomAccessFile.readFully(endHeader, i, commentLength + endHeaderLength);
                            return;
                        }
                    }
                }
            }
            throw new java.io.IOException("End header not found");
        }
        catch (java.io.IOException e){
            throw e;
        }

    }

    private CentralDirectoryHeader getHeader(String filename){
        if (newZip){
            return null;
        }
    /* //   System.out.print("trying to get "+filename+"'s header...");
        for (int i=0; i<centralDirectory.size(); i++){
            CentralDirectoryHeader currentHeader = (CentralDirectoryHeader)centralDirectory.get(i);
            if (currentHeader.fileName.equals(filename)){
        //       System.out.println("found it");
                return currentHeader;
            }
        }
        // System.out.println("not there");
        return null;*/
        return (CentralDirectoryHeader)filenameToHeaderMap.get(filename);
    }

    private void writeHeader(CentralDirectoryHeader header, int pos) throws java.io.IOException{
        header.relativeOffset = pos;
        header.localHeaderReference.position = pos;
        // System.out.println("writing local header "+header.fileName+" from "+pos+" to "+(pos+header.getLocalHeaderSpace()));
        if (header.data != null){
            crc.reset();
            crc.update(header.data);
        }
        header.localHeaderReference.writeAll();
        if (header.data != null){
            writeValue(header.data, 0, header.data.length,header.localHeaderReference.position+header.localHeaderReference.getThisHeaderSpace());
        }
        if (header.localHeaderReference.needsDataDescriptor()){
            header.localHeaderReference.writeDataDescriptor();
        }
    }

    private boolean placeHeader(CentralDirectoryHeader header) throws java.io.IOException {
        for(int i=0; i<holes.size(); i++){
            LocalizedVacuity currentHole = (LocalizedVacuity)holes.get(i);
            if (currentHole.size >= header.getLocalHeaderSpace()){
//                System.out.println("found hole that fits at "+currentHole.pos);
                writeHeader(header, currentHole.pos);
                holes.remove(currentHole);
                return true;
            }
        }
        return false;
    }



    private void placeHeaders() throws java.io.IOException{
        java.util.Collections.sort(toWrite, headerSizeComparator);
        findHoles();
//        System.out.println("\nHoles: ");
//        if (holes.size() == 0){
//            System.out.println("no holes");
//        }
//        for (int i=0; i<holes.size(); i++){
//            System.out.println(holes.get(i));
//        }
        boolean stillPlacing = true;
        for (int i=0; i<toWrite.size(); i++){
            CentralDirectoryHeader currentHeader = (CentralDirectoryHeader)toWrite.get(i);
//            System.out.println("need to place: \n"+currentHeader+"\n");
            if (stillPlacing){
                stillPlacing = placeHeader(currentHeader);
            }
            if (!stillPlacing){
                int amountWritten = currentHeader.getLocalHeaderSpace();
      //          System.out.println("writing it at the end at "+lastEntry+" wrote "+currentHeader.getLocalHeaderSpace());
                writeHeader(currentHeader, lastEntry);
                lastEntry += amountWritten;
  //              System.out.println("last entry is now at: "+lastEntry);
            }
            if (!centralDirectory.contains(currentHeader)){
                centralDirectory.add(currentHeader);
            }
            else{
                //    System.out.println("I didn't put this back in: "+currentHeader);
            }
            //System.out.println();
        }
        if (toWrite.size() > 0){ //only sort itf it needs sorting...maybe we don't even want to sort
            java.util.Collections.sort(centralDirectory, headerLocationComparator);
        }
    }

    private void writeCentralDirectoryAndEndHeader() throws java.io.IOException{
        newZip = false;
        setEnd();
 //       System.out.println("CD is at: "+lastEntry);
        int startLocation = lastEntry;
        int pos = startLocation;
        int size = 0;
        int totalEntries = centralDirectory.size();
        // System.out.println("\nwriting out "+totalEntries+" central directory records at "+startLocation);
        for(int i=0; i<totalEntries; i++){
            CentralDirectoryHeader currentHeader = (CentralDirectoryHeader)centralDirectory.get(i);
            currentHeader.position = pos;
            //   System.out.println("writing "+currentHeader.fileName+" from "+pos+" to "+(pos+currentHeader.getThisHeaderSpace()));
            currentHeader.writeAll();
            pos += currentHeader.getThisHeaderSpace();
            size += currentHeader.getThisHeaderSpace();
        }
        writeInt(pos, ENDSIG, 4);
        writeInt(pos+4, 0, 2);
        writeInt(pos+6, 0, 2);
        writeInt(pos+8, totalEntries, 2);
        writeInt(pos+10, totalEntries, 2);
        writeInt(pos+12, size, 4);
        writeInt(pos+16, startLocation, 4);
        writeInt(pos+20, 0, 2);
        try{
            m_randomAccessFile.setLength(pos+22);
        }
        catch (java.io.IOException e){
            throw e;
        }
        //System.out.println("end position :"+ (pos+22));
    }

    private void deleteDirectories(){
        java.util.Iterator i = centralDirectory.iterator();
        while (i.hasNext()){
            CentralDirectoryHeader currentHeader = (CentralDirectoryHeader)i.next();
            if (currentHeader.shouldDelete){
                // System.out.println("deleting "+currentHeader.fileName);
                i.remove();
            }
        }
    }

    private void printLocalAndCDHeaders(java.util.Vector v){
        System.out.println("*********************************LOCAL HEADERS:");
        for (int i=0; i<v.size(); i++){
            System.out.println(((CentralDirectoryHeader)v.get(i)).localHeaderReference);
        }
        System.out.println("\n*******************************CENTRAL DIRECTORY HEADERS:\n");
        for (int i=0; i<v.size(); i++){
            System.out.println(v.get(i)+"\n");
        }
    }

    public void close() throws java.io.IOException {
        if (outputStreamOpen){
            closeCurrentFile();
        }

        //debug
/*
        System.out.println("So here's where we stand: ");
        System.out.println("central directory, "+centralDirectory.size()+" entries");
        printLocalAndCDHeaders(centralDirectory);
        System.out.println("\nto write: "+toWrite.size()+" entries");
        printLocalAndCDHeaders(toWrite);
*/
        deleteDirectories();
        placeHeaders();
        writeCentralDirectoryAndEndHeader();
        fillHoles();

//        newZip = false;
//        System.out.println("the file size is: "+m_randomAccessFile.length());
//        setAtEndSig();
//        printEndHeader();
//        initCentralDirectory();
//
//        System.out.println("\nOpening the file again. There are "+centralDirectory.size()+"entries:");
//        printLocalAndCDHeaders(centralDirectory);


        allOpen = false;
        m_randomAccessFile.close();
        endHeader = null;
        centralDirectory = null;
        currentLocalFileData = null;
    }


    public java.io.OutputStream createFile( String filename ) throws IllegalArgumentException, java.io.IOException {
        return createFile(filename, true);
    }

    public java.io.OutputStream createFile( String filename, boolean compressItIfYouGotIt ) throws IllegalArgumentException, java.io.IOException {
        shouldCompressCurrent = compressItIfYouGotIt;
        if (m_randomAccessFile != null){
            if (outputStreamOpen){
                closeCurrentFile();
            }
            m_currentlyOpenStream = new java.io.ByteArrayOutputStream();
            currentFile = filename;
            outputStreamOpen = true;
        }
        else{
            throw  new java.io.IOException( "No zip file currently open" );
        }
//        System.out.println("opened: "+filename);
        return m_currentlyOpenStream;
    }

    public void keepFile( String filename ) throws edu.cmu.cs.stage3.io.KeepFileNotSupportedException{
        if (newZip){
            throw new edu.cmu.cs.stage3.io.KeepFileNotSupportedException();
        }
        String fullName = (currentDirectory+filename);
        CentralDirectoryHeader toKeep = getHeader(fullName);
//        System.out.print("trying to keep "+fullName+"...");
        if (toKeep != null){
            //       System.out.println("kept");
            toKeep.setShouldDelete(false);
        }
        else{
            throw new RuntimeException("Could not keep file \""+fullName+"\"");
        }
    }

    public boolean isKeepFileSupported(){
        return (!newZip);
    }

    public void createDirectory(String pathname) throws IllegalArgumentException, java.io.IOException{
        if( (pathname.indexOf( '/' ) != -1) || (pathname.indexOf( '\\' ) != -1) ) {
            throw new IllegalArgumentException( "pathname cannot contain path separators" );
        }
        if( pathname.length() <= 0 ) {
            throw new IllegalArgumentException( "pathname has no length" );
        }
        // currentDirectory = currentDirectory+pathname+"/";
    }

    public void checkAndUpdateHeader(String filename, byte[] data) throws java.io.IOException {
        CentralDirectoryHeader header = getHeader(filename);
        if (header != null){
            header.setShouldDelete(false);
            if (data != null){
                header.lastModTime = timeStamp;
                header.localHeaderReference.lastModTime = timeStamp;
 //               System.out.println("found "+header.fileName+" in file already at "+header.relativeOffset);
                updateHeader(header, data, false);
            }
        }
        else{
//            System.out.println("new zip entry");
            header = new CentralDirectoryHeader();
            header.localHeaderReference = new LocalHeader();
            header.setCompression(shouldCompressCurrent);
            header.lastModTime = timeStamp;
            header.localHeaderReference.lastModTime = timeStamp;
            header.setFileName(filename);
            if (newZip){
                header.position = lastEntry; //just for sorting purposes
                header.relativeOffset = lastEntry;
                header.localHeaderReference.position = lastEntry;
                updateHeader(header, data, true);
                centralDirectory.add(header);
                lastEntry += header.getLocalHeaderSpace();
            }
            else{ //since the size of the header is 0, this will add the header to the toWrite vector to be positioned later
//                System.out.println("gonna process this later");
                updateHeader(header,data, false);
            }
        }
    }

    public void closeCurrentFile() throws java.io.IOException {
        String filename = (currentDirectory+currentFile);
//        System.out.println("closed: "+filename);
        outputStreamOpen = false;
        checkAndUpdateHeader(filename, m_currentlyOpenStream.toByteArray());
        m_currentlyOpenStream.close();
    }

    private boolean doesFileExist(String filename){
    	for (int i=0; i<centralDirectory.size(); i++){
    		CentralDirectoryHeader cdh = (CentralDirectoryHeader)centralDirectory.get(i);
    		if (cdh.fileName.equals(currentDirectory+filename)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public Object getKeepKey( String filename ) {
        if (newZip || !doesFileExist(filename)){
//        	if (!newZip){
//        		System.out.println("Can't find "+currentDirectory+filename+" so returning returning NULL");
//        	} else{
//        		System.out.println("new zip on "+filename+" so returning returning NULL");
//        	}
            return null;
        }
        return ZipFileTreeLoader.getKeepKey( rootFile, currentDirectory, filename );
    }
/*
    public void inspectZipFile(){
        printLocalAndCDHeaders(centralDirectory);
        findHoles();
        System.out.println("\n**************************************HOLES\n");
        for (int i=0; i<holes.size(); i++){
            System.out.println(holes.get(i));
        }
    }

    public static void main(String[] args){
        ZipFileTreeStorer storer = new ZipFileTreeStorer();
        String filename = "C:\\test.a2w";
        java.util.Vector fileVector = new java.util.Vector();
        System.out.println("inspecting: "+filename);
        try{
            storer.open(filename);
//            storer.fillHoles();
            storer.inspectZipFile();
//            java.io.OutputStream o = storer.createFile("Dave.rocks");
//            String toWrite = "Dave rox0rs!";
//            o.write(toWrite.getBytes());
//            storer.closeCurrentFile();
//            //storer.createDirectory("yahtzee");
//            System.out.println("\n\n\nMy Storer\n");
//            //
//            storer.close();

//            storer = new ZipFileTreeStorer();
//            java.io.OutputStream out = new java.io.FileOutputStream( "C:\\ziptest3.zip" );
//            java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream( new java.io.BufferedOutputStream( out ) );
//            java.util.zip.ZipEntry currentEntry = new java.util.zip.ZipEntry("Dave.rocks" );
//            zipOut.putNextEntry( currentEntry );
//            zipOut.write(toWrite.getBytes());
//            zipOut.flush();
//            zipOut.closeEntry();
//            zipOut.flush();
//            zipOut.finish();
//            zipOut.close();
//            storer.open("C:\\ziptest3.zip");
//            System.out.println("\n\n\nJAVA's Storer");
//            storer.inspectZipFile();
        }
        catch (java.lang.Exception e){
            e.printStackTrace();
        }
    }
*/
}