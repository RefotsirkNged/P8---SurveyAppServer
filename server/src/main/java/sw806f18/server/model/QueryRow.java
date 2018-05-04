package sw806f18.server.model;

public class QueryRow {
    private String tag;
    private boolean not;
    private OperatorType operator;
    private String value;
    private ValueType valueType;
    private LinkType link;

    /**
     * Constructor.
     *
     * @param tag Tag.
     * @param not Not.
     * @param operator Operator.
     * @param value Value.
     * @param link Link.
     */
    public QueryRow(String tag, boolean not, OperatorType operator,
                    String value, LinkType link) {
        this.tag = tag;
        this.not = not;
        this.operator = operator;
        this.value = value;
        this.link = link;

        if (isInteger(value)) {
            valueType = ValueType.INT;
        } else if (value.equals("true") || value.equals("false")) {
            valueType = ValueType.BOOL;
        } else {
            valueType = ValueType.TEXT;
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public OperatorType getOperator() {
        return operator;
    }

    public void setOperator(OperatorType operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public LinkType getLink() {
        return link;
    }

    public void setLink(LinkType link) {
        this.link = link;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}