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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

/**
 * @author Jason Pratt
 */
public class VariablesViewController extends edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty variables;
	protected RefreshListener refreshListener = new RefreshListener();
	// protected SyncListener syncListener = new SyncListener();
	protected boolean sleeping = false;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());

	public VariablesViewController() {
		setOpaque(false);
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
		setBorder(null);
	}

	public void set(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty variables) {
		clean();
		this.variables = variables;
		if (!sleeping) {
			startListening();
		}
		refreshGUI();
	}

	@Override
	public void goToSleep() {
		if (!sleeping) {
			stopListening();
			sleeping = true;
		}
	}

	@Override
	public void wakeUp() {
		if (sleeping) {
			startListening();
			sleeping = false;
		}
	}

	@Override
	public void die() {
		clean();
	}

	@Override
	public void clean() {
		stopListening();
		removeAll();
		variables = null;
	}

	@Override
	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
	}

	public void refreshGUI() {
		removeAll();
		Object[] vars = variables.getArrayValue();
		for (int i = 0; i < vars.length; i++) {
			final edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) vars[i];
			if (variable != null) {
				javax.swing.JComponent gui = null;
				if (gui == null) {
					edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory setImmediatelyFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(variable.value);
					gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(variable.value, true, true, false, setImmediatelyFactory);
				}
				add(gui);
				if ("true".equalsIgnoreCase((String) edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getMiscItem("javaLikeSyntax"))) {
					if (i < vars.length - 1) {
						add(new javax.swing.JLabel(", "));
					}
				}
			}
		}
	}

	protected void startListening() {
		if (variables != null) {
			// if( variables.getOwner() instanceof
			// edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse )
			// {
			// edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse
			// callToUserDefinedResponse =
			// (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)variables.getOwner();
			// if(
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue()
			// != null ) {
			// if( variables ==
			// callToUserDefinedResponse.requiredActualParameters ) {
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().requiredFormalParameters.addObjectArrayPropertyListener(
			// syncListener );
			// Object[] vars =
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().requiredFormalParameters.getArrayValue();
			// for( int i = 0; i < vars.length; i++ ) {
			// if( vars[i] != null ) {
			// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.addPropertyListener(
			// syncListener );
			// }
			// }
			// } else if( variables ==
			// callToUserDefinedResponse.keywordActualParameters ) {
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().keywordFormalParameters.addObjectArrayPropertyListener(
			// syncListener );
			// Object[] vars =
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().keywordFormalParameters.getArrayValue();
			// for( int i = 0; i < vars.length; i++ ) {
			// if( vars[i] != null ) {
			// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.addPropertyListener(
			// syncListener );
			// }
			// }
			// }
			// } else {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// callToUserDefinedResponse + "'s response is null", null );
			// }
			// } else if( variables.getOwner() instanceof
			// edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion
			// ) {
			// edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion
			// callToUserDefinedQuestion =
			// (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)variables.getOwner();
			// if(
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue()
			// != null ) {
			// if( variables ==
			// callToUserDefinedQuestion.requiredActualParameters ) {
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().requiredFormalParameters.addObjectArrayPropertyListener(
			// syncListener );
			// Object[] vars =
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().requiredFormalParameters.getArrayValue();
			// for( int i = 0; i < vars.length; i++ ) {
			// if( vars[i] != null ) {
			// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.addPropertyListener(
			// syncListener );
			// }
			// }
			// } else if( variables ==
			// callToUserDefinedQuestion.keywordActualParameters ) {
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().keywordFormalParameters.addObjectArrayPropertyListener(
			// syncListener );
			// Object[] vars =
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().keywordFormalParameters.getArrayValue();
			// for( int i = 0; i < vars.length; i++ ) {
			// if( vars[i] != null ) {
			// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.addPropertyListener(
			// syncListener );
			// }
			// }
			// }
			// } else {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// callToUserDefinedQuestion + "'s question is null", null );
			// }
			// }

			variables.addObjectArrayPropertyListener(refreshListener);
			Object[] vars = variables.getArrayValue();
			for (Object var : vars) {
				if (var != null) {
					((edu.cmu.cs.stage3.alice.core.Variable) var).name.addPropertyListener(refreshListener);
				}
			}
		}
	}

	protected void stopListening() {
		if (variables != null) {
			// if( variables.getOwner() instanceof
			// edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse )
			// {
			// edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse
			// callToUserDefinedResponse =
			// (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)variables.getOwner();
			// if(
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue()
			// != null ) {
			// if( variables ==
			// callToUserDefinedResponse.requiredActualParameters ) {
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().requiredFormalParameters.removeObjectArrayPropertyListener(
			// syncListener );
			// Object[] vars =
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().requiredFormalParameters.getArrayValue();
			// for( int i = 0; i < vars.length; i++ ) {
			// if( vars[i] != null ) {
			// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.removePropertyListener(
			// syncListener );
			// }
			// }
			// } else if( variables ==
			// callToUserDefinedResponse.keywordActualParameters ) {
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().keywordFormalParameters.removeObjectArrayPropertyListener(
			// syncListener );
			// Object[] vars =
			// callToUserDefinedResponse.userDefinedResponse.getUserDefinedResponseValue().keywordFormalParameters.getArrayValue();
			// for( int i = 0; i < vars.length; i++ ) {
			// if( vars[i] != null ) {
			// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.removePropertyListener(
			// syncListener );
			// }
			// }
			// }
			// } else {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// callToUserDefinedResponse + "'s response is null", null );
			// }
			// } else if( variables.getOwner() instanceof
			// edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion
			// ) {
			// edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion
			// callToUserDefinedQuestion =
			// (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)variables.getOwner();
			// if(
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue()
			// != null ) {
			// if( variables ==
			// callToUserDefinedQuestion.requiredActualParameters ) {
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().requiredFormalParameters.removeObjectArrayPropertyListener(
			// syncListener );
			// Object[] vars =
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().requiredFormalParameters.getArrayValue();
			// for( int i = 0; i < vars.length; i++ ) {
			// if( vars[i] != null ) {
			// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.removePropertyListener(
			// syncListener );
			// }
			// }
			// } else if( variables ==
			// callToUserDefinedQuestion.keywordActualParameters ) {
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().keywordFormalParameters.removeObjectArrayPropertyListener(
			// syncListener );
			// Object[] vars =
			// callToUserDefinedQuestion.userDefinedQuestion.getUserDefinedQuestionValue().keywordFormalParameters.getArrayValue();
			// for( int i = 0; i < vars.length; i++ ) {
			// if( vars[i] != null ) {
			// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.removePropertyListener(
			// syncListener );
			// }
			// }
			// }
			// } else {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// callToUserDefinedQuestion + "'s question is null", null );
			// }
			// }

			variables.removeObjectArrayPropertyListener(refreshListener);
			Object[] vars = variables.getArrayValue();
			for (Object var : vars) {
				if (var != null) {
					((edu.cmu.cs.stage3.alice.core.Variable) var).name.removePropertyListener(refreshListener);
				}
			}
		}
	}

	class RefreshListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener, edu.cmu.cs.stage3.alice.core.event.PropertyListener {
		@Override
		public void objectArrayPropertyChanging(edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		}
		@Override
		public void objectArrayPropertyChanged(edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
			if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED) {
				edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) ev.getItem();
				if (variable != null) {
					variable.name.addPropertyListener(this);
				}
			} else if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED) {
				edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable) ev.getItem();
				if (variable != null) {
					variable.name.removePropertyListener(this);
				}
			}
			refreshGUI();
		}
		@Override
		public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
		}
		@Override
		public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
			refreshGUI();
		}
	}

	// class SyncListener implements
	// edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener,
	// edu.cmu.cs.stage3.alice.core.event.PropertyListener {
	// private Object oldName;
	// public void objectArrayPropertyChanging(
	// edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
	// public void objectArrayPropertyChanged(
	// edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
	// try {
	// if( ev.getChangeType() ==
	// edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED
	// ) {
	// edu.cmu.cs.stage3.alice.core.Variable variable =
	// (edu.cmu.cs.stage3.alice.core.Variable)ev.getItem();
	// if( variables.getOwner().getChildNamed( variable.name.getStringValue() )
	// == null ) {
	// variable.name.addPropertyListener( this );
	// edu.cmu.cs.stage3.alice.core.Variable newVariable = new
	// edu.cmu.cs.stage3.alice.core.Variable();
	// newVariable.name.set( variable.name.get() );
	// newVariable.valueClass.set( variable.valueClass.get() );
	// newVariable.value.set( variable.value.get() );
	// variables.getOwner().addChild( newVariable );
	// variables.add( ev.getNewIndex(), newVariable );
	// }
	// } else if( ev.getChangeType() ==
	// edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED
	// ) {
	// edu.cmu.cs.stage3.alice.core.Variable variable =
	// (edu.cmu.cs.stage3.alice.core.Variable)ev.getItem();
	// variable.name.removePropertyListener( this );
	// edu.cmu.cs.stage3.alice.core.Variable variableToRemove =
	// (edu.cmu.cs.stage3.alice.core.Variable)variables.get( ev.getOldIndex() );
	// variables.remove( variableToRemove );
	// variables.getOwner().removeChild( variableToRemove );
	// }
	// } catch( Throwable t ) {
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
	// "Error synchronizing parameters.", t );
	// }
	// }
	// public void propertyChanging(
	// edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {
	// oldName = ev.getProperty().getValue();
	// }
	// public void propertyChanged(
	// edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {
	// try {
	// Object[] vars = variables.getArrayValue();
	// for( int i = 0; i < vars.length; i++ ) {
	// if( vars[i] != null ) {
	// if( ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.get().equals(
	// oldName ) ) {
	// ((edu.cmu.cs.stage3.alice.core.Variable)vars[i]).name.set( ev.getValue()
	// );
	// break;
	// }
	// }
	// }
	// } catch( Throwable t ) {
	// //HACK
	// }
	// }
	// }
}
