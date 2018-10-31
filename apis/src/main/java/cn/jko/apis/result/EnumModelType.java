package cn.jko.apis.result;

public enum EnumModelType {

    INT("int"),
    DOUBLE("double"),
    STRING("string"),
    BOOLEAN("boolean"),
    OBJECT("{"),
    ARRAY("["),;
    String start;

    public String getStart() {
        return start;
    }

    EnumModelType(String start) {
        this.start = start;
    }

    public String desc() {
        switch (this) {
            case OBJECT:
                return "object";
            case STRING:
                return "string";
            case ARRAY:
                return "list";
            case INT:
                return "int";
            case DOUBLE:
                return "float";
            case BOOLEAN:
                return "boolean";
        }
        return "object";
    }
}
