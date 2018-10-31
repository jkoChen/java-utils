package cn.jko.apis.result;


import cn.jko.common.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 返回的模型 用于生成实例文档和模型
 *
 * int double string boolean obj arrSize
 */
public class ClassModel {
    //针对arr 生成的数组长度
    private static final int arrSize = 2;
    /**
     * 给定种子 让每次生成的文档都固定
     * 否则每次生成的文档都是随机的
     */
    Random ran = new Random(1);
    private boolean start = false;
    private String name;
    private EnumModelType type;
    private String desc;
    /**
     * 取值示例  支持 范围或穷举距离
     */
    private String example;
    private List<ClassModel> subModel = new ArrayList<>();

    public void setStart(boolean start) {
        this.start = start;
    }

    public List<ClassModel> getSubModel() {
        return subModel;
    }

    public void setSubModel(List<ClassModel> subModel) {
        this.subModel = subModel;
    }

    /**
     * @param content name:type(example) desc
     * @return
     */
    public static ClassModel of(String content) {
        ClassModel classModel = new ClassModel();
        String[] vals = content.split(":", 2);

        String typeAndDesc = content.trim();
        if (vals.length > 1 && !vals[0].contains("{") && !vals[0].contains("(") && !vals[0].contains("[")) {
            classModel.setName(vals[0].trim());
            typeAndDesc = vals[1].trim();
        }

        String childrenContents = null;
        String descStr = null;


        if (typeAndDesc.startsWith("[")) {
            classModel.type = EnumModelType.ARRAY;
            childrenContents = StringUtils.getBracketContents(typeAndDesc, 1);
            descStr = StringUtils.getBracketDesc(typeAndDesc, 1);
            String ex = StringUtils.getBracketContents(descStr, 3);
            if (!StringUtils.isEmpty(ex)) {
                classModel.example = ex.trim();
            }
            descStr = StringUtils.getBracketDesc(descStr, 3);

        } else if (typeAndDesc.startsWith("{")) {
            classModel.type = EnumModelType.OBJECT;
            childrenContents = StringUtils.getBracketContents(typeAndDesc, 2);
            descStr = StringUtils.getBracketDesc(typeAndDesc, 2);
        }

        if (descStr != null) {
            classModel.desc = descStr.trim();
            classModel.subModel = new ArrayList<>();
            if (classModel.type == EnumModelType.OBJECT) {
                if (childrenContents != null) {
                    StringUtils.getChildrenContents(childrenContents).forEach(s -> {
                        classModel.subModel.add(ClassModel.of(s));
                    });
                }
            } else {
                classModel.subModel.add(ClassModel.of(childrenContents));
            }


        } else {
            String[] vls = typeAndDesc.trim().split("\\s+", 2);
            String typeStr = vls[0].trim().replaceAll("\\(.*?\\)", "");
            String exampleStr = StringUtils.getMatchStr(vls[0].trim(), "\\((.*?)\\)");
            classModel.type = EnumModelType.valueOf(typeStr.toUpperCase());
            classModel.example = exampleStr == null ? null : exampleStr.trim();
            if (vls.length == 2) {
                classModel.desc = vls[1].trim();
            }
        }
        return classModel;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean flag = true;
        List<ClassModel> chid = this.subModel;
        String desc = this.type.desc();
        if (type == EnumModelType.ARRAY) {
            ClassModel c = this.subModel.get(0);
            desc = desc + ":" + c.type.desc();
            if (c.type == EnumModelType.OBJECT) {
                chid = c.subModel;
            } else {
                chid = null;
            }
        }
        if (!start && this.name != null) {
            flag = false;
            sb.append(this.name + " [" + desc + "]" + ((this.desc == null || this.desc.trim().isEmpty()) ? "" : " (" + this.desc + ")") + "\n");
        }

        final boolean f = flag;
        if (chid != null && chid.size() > 0) {
            for (ClassModel t : chid) {
                String val = t.toString();
                sb.append(Arrays.stream(val.split("\n")).map(s -> {
                    if (f) {
                        return s;
                    } else {
                        if (s.contains("|--")) {
                            return " |  " + s;
                        } else {
                            return " |--" + s;
                        }
                    }

                }).collect(Collectors.joining("\n"))).append("\n");
            }

        }
        return sb.toString();


    }

    public String to() {
        return to(false);
    }

    public String to(boolean notEnd) {
        StringBuilder sb = new StringBuilder();
        if (!start && !StringUtils.isEmpty(name)) {
            sb.append("\"" + name + "\"").append(":");
        }
        switch (type) {
            case INT:
            case DOUBLE:
            case BOOLEAN:
            case STRING:
                sb.append(mockExample()).append(toComma(notEnd)).append(toDesc());

                break;
            case ARRAY:
                sb.append("[");
                sb.append(toDesc());
                EnumModelType ctype = subModel.get(0).type;
                if (ctype != EnumModelType.OBJECT && ctype != EnumModelType.ARRAY && ctype != EnumModelType.STRING) {
                    sb.append('\0');
                }
                childrenTo(sb);
                sb.append("]").append(toComma(notEnd));


                break;
            case OBJECT:
                sb.append("{").append(toDesc());
                childrenTo(sb);
                sb.append("}").append(toComma(notEnd));
                break;

        }


        return sb.toString();
    }

    private void childrenTo(StringBuilder sb) {
        for (int i = 0; i < subModel.size(); i++) {
            boolean f = (i != (subModel.size() - 1));
            if (this.type == EnumModelType.ARRAY) {
                int si = arrSize;
                if (!StringUtils.isEmpty(example)) {
                    si = Integer.parseInt(example);
                }
                for (int j = 0; j < si; j++) {
                    boolean g = (j != si - 1);
                    sb.append(subModel.get(i).to(g));
                }
            } else {
                sb.append(subModel.get(i).to(f));

            }
        }

    }

    private String toDesc() {
        return StringUtils.isEmpty(desc) ? "" : ("//" + desc);
    }

    private String toComma(boolean notEnd) {
        return notEnd ? "," : "";
    }

    private String mockExample() {
        if (example == null) {
            if (type == EnumModelType.STRING) {
                return "\"" + (desc == null ? name : desc) + "\"";
            } else {
                if (type == EnumModelType.BOOLEAN) {
                    return (ran.nextInt(100) > 50) + "";
                } else if (type == EnumModelType.INT) {
                    return ran.nextInt(100) + "";
                } else {
                    return ((int) ran.nextDouble() * 10000) / 100.0 + "";
                }
            }
        }
        String v = null;
        String[] vals = example.split(",");
        if (vals.length == 1) {
            if (type == EnumModelType.STRING) {
                return "\"" + example + "\"";
            }
            vals = example.split("-", 2);
            if (vals.length == 1) {
                v = example;
            }
        } else {
            v = vals[ran.nextInt(vals.length)];
        }
        if (v != null) {
            if (type == EnumModelType.STRING) {
                return "\"" + v + "\"";
            } else {
                return v;
            }
        }

        switch (type) {
            case INT:
                int s = Integer.parseInt(vals[0]);
                int e = Integer.parseInt(vals[1]);
                return "" + (ran.nextInt(e - s + 1) + s);
            case DOUBLE:
                double sd = Double.parseDouble(vals[0]);
                double ed = Double.parseDouble(vals[1]);
                return "" + Math.round((ran.nextDouble() * (ed - sd) + sd) * 1000) / 1000.0;
        }
        return null;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setType(EnumModelType type) {
        this.type = type;
    }


    public void setDesc(String desc) {
        this.desc = desc;
    }


    public void setExample(String example) {
        this.example = example;
    }
}
