package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Expression;

public abstract class AbstractIsEqualTo extends BinaryObjectResultingInBooleanQuestion {
	protected boolean isEqualTo( Object aValue, Object bValue ) {
		if( aValue != null ) {
			if( aValue.equals( bValue ) ) {
                return true;
            } else {
                if( aValue instanceof Expression ) {
                    Expression aExpression = (Expression)aValue;
                    Object aValue2 = aExpression.getValue();
                    if( bValue instanceof Expression ) {
                        Expression bExpression = (Expression)bValue;
                        Object bValue2 = bExpression.getValue();
                        if( aExpression.equals( bExpression.getValue() ) ) {
                            return true;
                        } else {
                            if( aValue2 != null ) {
                                if( aValue2.equals( bExpression ) ) {
                                    return true;
                                } else {
                                    return aValue2.equals( bValue2 );
                                }
                            } else {
                                return bValue2==null;
                            }
                        }
                    } else {
                        if( aValue2 != null ) {
                            return aValue2.equals( bValue );
                        } else {
                            return bValue == null;
                        }
                    }
                } else {
                    if( bValue instanceof Expression ) {
                        Expression bExpression = (Expression)bValue;
                        Object bValue2 = bExpression.getValue();
                        if( aValue.equals( bExpression ) ) {
                            return true;
                        } else {
                            return aValue.equals( bValue2 );
                        }
                    } else {
                        return false;
                    }
                }
            }
		} else {
            if( bValue instanceof Expression ) {
                Expression bExpression = (Expression)bValue;
                Object bValue2 = bExpression.getValue();
                return bValue2 == null;
            } else {
                return bValue == null;
            }
		}
	}
}