package edu.uci.ics.asterix.aql.literal;

import edu.uci.ics.asterix.aql.base.ILiteral;

public class TrueLiteral implements ILiteral {
    private static final long serialVersionUID = -8513245514578847512L;

    private TrueLiteral() {
    }

    public final static TrueLiteral INSTANCE = new TrueLiteral();

    @Override
    public Type getLiteralType() {
        return Type.TRUE;
    }

    @Override
    public String getStringValue() {
        return "true";
    }

    @Override
    public String toString() {
        return getStringValue();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == INSTANCE;
    }

    @Override
    public int hashCode() {
        return (int) serialVersionUID;
    }
}