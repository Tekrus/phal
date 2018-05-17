import enums.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CodeGeneration extends Visitor {

    private PrintWriter writer;
    private final List<Type> ComponentIncludesMap;
    private int loopTime = 0;

    CodeGeneration(List<Type> CIM) {
        ComponentIncludesMap = CIM;
        try {
            writer = new PrintWriter(new FileWriter(MainClass.inputFileName + ".ino", false));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void printHeader() {
        writer.print("/* Phal AutoGenerated .ino file \n"
                + "*  Created " + new SimpleDateFormat("dd-MM-yyyy").format(new Date())
                + "\n*/\n\n"
        );
    }

    private void printCmpIncludes() {
        for (Type t : ComponentIncludesMap) {
            writer.print("#include \"" + t + "/" + t + ".h\" \n"
            );
        }

    }

    @Override
    public void visit(ProgramNode node) {
        printHeader();
        printCmpIncludes();

        for (IncludeNode incl : node.includeNodes)
            visit(incl);

        for (SetupCntNode dcl : node.setupNode.setupCntNodes) {
            if (dcl.dclNode instanceof VarDclNode)
                visit((VarDclNode) dcl.dclNode, CodeGen.GLOBAL);
            if (dcl.dclNode instanceof ListNode)
                visit((ListNode) dcl.dclNode, CodeGen.GLOBAL);
            if (dcl.dclNode instanceof GroupNode)
                visit((GroupNode) dcl.dclNode, CodeGen.GLOBAL);
            if (dcl.dclNode instanceof CmpDclNode) {
                visit((CmpDclNode) dcl.dclNode, CodeGen.GLOBAL);
            }
        }


        visit(node.setupNode);
        visit(node.repeatNode);

        for (FuncNode func : node.funcNodes) {
            visit(func);
        }

        writer.close();
    }


    @Override
    public void visit(SetupNode node) {
        writer.print("void setup(){ \n");

        for (SetupCntNode cnt : node.setupCntNodes) {
            if (cnt.dclNode instanceof VarDclNode)
                visit((VarDclNode) cnt.dclNode, CodeGen.SETUP);
            if (cnt.dclNode instanceof ListNode)
                visit((ListNode) cnt.dclNode, CodeGen.SETUP);
            if (cnt.dclNode instanceof GroupNode)
                visit((GroupNode) cnt.dclNode, CodeGen.SETUP);

            if (cnt.dclNode instanceof CmpDclNode) {
                visit((CmpDclNode) cnt.dclNode, CodeGen.SETUP);
            }

            if (cnt.stmtNode != null) {
                visit(cnt.stmtNode);
            }
        }


        writer.print("} \n\n");
    }

    @Override
    public void visit(RepeatNode node) {
        writer.print("void loop(){ \n");
        super.visit(node);
        writer.print("} \n\n");
    }

    @Override
    public void visit(WaitNode node) {
        writer.print("delay((");
        visit(node.exprNode);
        writer.print(")*1000);\n");
    }

    @Override
    public void visit(AssignmentNode node) {
        if (node.idNode.type == Type.GROUP) {
            if (node.assignmentOperator == AssignmentOperator.EQUALS) {
                visit(node.idNode);
                LiteralExprNode le = (LiteralExprNode) node.exprNode;

                switch (le.literalExprNode) {
                    case "true":
                    case "on":
                        writer.print(".on();\n");
                        break;
                    case "false":
                    case "off":
                        writer.print(".off();\n");
                        break;
                }
            }
        } else if (node.idNode.type == Type.LIST) {
            visit(node.idNode);
            if (node.assignmentOperator == AssignmentOperator.PLUSEQUALS) {
                writer.print(".add(");
                writer.print(")");
            } else if (node.assignmentOperator == AssignmentOperator.MINUSEQUALS) {
                writer.print(".remove(");
                writer.print(")");
            } else if (node.assignmentOperator == AssignmentOperator.EQUALS) {
                writer.print(" = ");
            }
            visit(node.exprNode);
            writer.print(";\n");
        } else {
            visit(node.idNode);
            if (node.assignmentOperator == AssignmentOperator.EQUALS) {
                writer.print(" = ");
            } else {
                writer.print(node.assignmentOperator.toString());
            }
            visit(node.exprNode);
            writer.print(";\n");
        }
    }

    @Override
    public void visit(LoopTimesNode node) {
        String indexer = "_i" + loopTime;
        writer.print("for(int " + indexer + " = 0; " + indexer + " < ");
        visit(node.exprNode);
        writer.print("; " + indexer + "++){\n");
        for (StmtNode stmt : node.stmtNodes) {
            visit(stmt);
        }
        writer.print("}");
        loopTime++;
    }

    @Override
    public void visit(LoopUntilNode node) {
        writer.print("while(!(");
        visit(node.exprNode);
        writer.print(")){\n");
        visit(node.idNode);
        if (node.loopOperator == LoopUntilOperator.DECREASE) {
            writer.print(" -= ");
        } else {
            writer.print(" += ");
        }
        visit(node.numberNode);
        writer.print(";\n");

        for (StmtNode stmt : node.stmtNodes) {
            visit(stmt);
        }
        writer.print("}\n");

    }

    @Override
    public void visit(IdNode node) {
        if (node.subId == null)
            writer.print(node.id);
        else
            writer.print(node.id + "." + node.subId);
    }


    public void visit(VarDclNode node, Enum<CodeGen> location) {

        if (location == CodeGen.GLOBAL) {
            visit(node.typeNode);
            writer.print(" ");
            visit(node.idNode);
        }


        if (location == CodeGen.SETUP) {
            if (node.exprNode != null) {
                visit(node.idNode);
                writer.print(" = ");
                visit(node.exprNode);
            }
        }

        if (location == CodeGen.FUNCTION) {
            visit(node.typeNode);
            writer.print(" ");
            visit(node.idNode);
            if (node.exprNode != null) {
                writer.print(" = ");
                visit(node.exprNode);
            }
        }

        writer.print(";\n");

    }

    @Override
    public void visit(ReturnStmtNode node) {
        writer.print("return ");
        visit(node.exprNode);
        writer.print(";");
    }

    public void visit(CmpDclNode node, Enum<CodeGen> location) {
        if (location == CodeGen.GLOBAL) {
            switch (node.advTypeNode.Type) {
                case TEMPERATURESENSOR:
                    writer.print("TemperatureSensor ");
                    break;
                case LIGHTBULB:
                    writer.print("Lightbulb ");
                    break;
                case MOTOR:
                    writer.print("Motor ");
                    break;
                default:
                    writer.print(node.advTypeNode.type);
                    break;
            }

            visit(node.idNode);
            writer.print("(");
            for (int i = 0; i < node.literalExprNodes.size(); i++) {
                if (i != 0) {
                    writer.print(", ");
                }
                visit(node.literalExprNodes.get(i));
            }
            writer.print(")");

            writer.print(";\n");
        }

        if (location == CodeGen.SETUP) {
            switch (node.advTypeNode.Type) {
                case TEMPERATURESENSOR:
                    for (LiteralExprNode exp : node.literalExprNodes) {
                        writer.print("pinMode(");
                        writer.print(exp.literalExprNode);
                        writer.print(", INPUT);\n");
                    }
                    break;
                case LIGHTBULB:
                case MOTOR:
                    for (LiteralExprNode exp : node.literalExprNodes) {
                        writer.print("pinMode(");
                        writer.print(exp.literalExprNode);
                        writer.print(", OUTPUT);\n");
                    }
                    break;
            }
        }
    }

    @Override
    public void visit(InfixExprNode node) {
        visit(node.leftExprNode);

        switch (node.infixOperator) {
            case AND:
                writer.print(" && ");
                break;
            case OR:
                writer.print(" || ");
                break;
            case EQUAL:
                writer.print(" == ");
                break;
            default:
                writer.print(node.infixOperator.toString());
                break;

        }

        visit(node.rightExprNode);
    }


    @Override
    public void visit(LiteralExprNode node) {
        switch (node.literalExprNode) {
            case "on":
            case "true":
                writer.print("true");
                break;
            case "off":
            case "false":
                writer.print("false");
                break;
            default:
                writer.print(node.literalExprNode);
                break;
        }
    }

    @Override
    public void visit(IfStmtNode node) {
        writer.write("if(");
        visit(node.ifExprNode);
        writer.write("){\n");
        visit(node.ifBlock);
        writer.write("\n}\n");

        for (ElseIfStmtNode elif : node.elseIfStmts)
            visit(elif);

        if (node.elseBlock != null)
            visit(node.elseBlock);
    }

    @Override
    public void visit(ElseIfStmtNode node) {
        writer.print("else if(");
        visit(node.exprNode);
        writer.print("){\n");
        visit(node.block);
        writer.print("}\n");
    }

    @Override
    public void visit(ElseBlockNode node) {
        writer.print("else{\n");
        for (StmtNode stmt : node.stmtNodes)
            visit(stmt);
        writer.print("}");
    }


    @Override
    public void visit(FuncNode node) {
        visit(node.typeNode);
        writer.write(" ");
        visit(node.idNode);
        writer.write(" (");
        visit(node.parametersNode);
        writer.write("){\n");
        for (FuncCntNode cnt : node.funcCntNodes) {
            if (cnt.varDclNode instanceof VarDclNode)
                visit(cnt.varDclNode, CodeGen.FUNCTION);
            else if (cnt.listNode instanceof ListNode)
                visit(cnt.listNode, CodeGen.FUNCTION);
            else
                visit(cnt);
        }

        writer.write("\n}\n");
    }

    @Override
    public void visit(ParamNode node) {
        visit(node.typeNode);
        writer.print(" ");
        visit(node.idNode);
    }

    @Override
    public void visit(ParametersNode node) {
        for (int i = 0; i < node.paramNodes.size(); i++) {
            if (i != 0) {
                writer.print(", ");
            }
            visit(node.paramNodes.get(i));
        }
    }

    @Override
    public void visit(TypeNode node) {
        if (node.islist) {
            writer.print("LinkedList<");
        }

        switch (node.Type) {
            case NONE:
                writer.print("void");
                break;
            case NUMBER:
                if(node.isInt){
                    writer.print("int");
                }else{
                    writer.print("float");
                }
                break;
            case BOOL:
                writer.print("bool");
                break;
            case TEXT:
                writer.print("String");
                break;
            case MOTOR:
                writer.print("motor");
                break;
            case LIGHTBULB:
                writer.print("lightbulb");
                break;
            case TEMPERATURESENSOR:
                writer.print("temperatureSensor");
                break;
            case GROUP:
                writer.print("group");
                break;
            default:
                writer.print("void");
                break;
        }

        if (node.islist) {
            writer.print(">");
        }

    }

    public void visit(ListNode node, Enum<CodeGen> location) {
        if (location == CodeGen.GLOBAL) {
            visit(node.typeNode);
            writer.print(" ");
            visit(node.idNode);

            writer.print(" = ");
            visit(node.typeNode);

            writer.print("();\n");
        }

        if (location == CodeGen.SETUP) {
            for (ExprNode expr : node.memberExprNodes) {
                visit(node.idNode);

                writer.print(".add(");
                visit(expr);
                writer.print(");\n");
            }
        }

        if (location == CodeGen.FUNCTION) {
            visit(node.typeNode);
            writer.print(" ");
            visit(node.idNode);

            writer.print(" = ");
            visit(node.typeNode);

            writer.print("();\n");

            for (ExprNode expr : node.memberExprNodes) {
                visit(node.idNode);
                writer.print(".add(");
                visit(expr);
                writer.print(");\n");
            }
        }

    }

    public void visit(GroupNode node, Enum<CodeGen> location) {
        if (location == CodeGen.GLOBAL) {
            writer.print("PhalGroup<Adt> ");
            visit(node.idNode);
            writer.print(" = PhalGroup<Adt>(" + node.memberIdNodes.size() + ");\n");
        }

        if (location == CodeGen.SETUP) {
            for (IdNode id : node.memberIdNodes) {
                visit(node.idNode);
                writer.print(".add(" + id.id + ");\n");
            }
        }

        if (location == CodeGen.FUNCTION) {
            writer.print("PhalGroup ");
            visit(node.idNode);
            writer.print(" = PhalGroup<Adt>(" + node.memberIdNodes.size() + ");\n");

            for (IdNode id : node.memberIdNodes) {
                visit(node.idNode);
                writer.print(".add(" + id.id + ");\n");
            }
        }
    }

    @Override
    public void visit(SwitchStmtNode node) {
        writer.print("switch(");
        visit(node.exprNode);
        writer.print("){\n");
        visit(node.caseListNode);
        writer.print("}\n");
    }

    @Override
    public void visit(CaseListNode node) {
        for (CaseStmtNode cmt : node.caseStmtNodes) {
            visit(cmt);
        }

        visit(node.defaultCaseNode);
    }

    @Override
    public void visit(CaseStmtNode node) {
        writer.print("case ");
        visit(node.exprNode);
        writer.print(":\n");
        for (StmtNode stmt : node.stmtNodes)
            visit(stmt);
        writer.print("break;\n");
    }

    @Override
    public void visit(DefaultCaseNode node) {
        writer.print("default: \n");
        for (StmtNode stmt : node.stmtNodes)
            visit(stmt);
        writer.print("break;\n");
    }

    @Override
    public void visit(ParensExprNode node) {
        writer.print("(");
        visit(node.exprNode);
        writer.print(")");
    }

    @Override
    public void visit(UnaryExprNode node) {
        writer.print(node.unaryOperator.toString());
        visit(node.exprNode);
    }

    @Override
    public void visit(LiteralAdvancedNode node) {
        //'get' 'element' expr 'from' ID
        visit(node.idNode);
        writer.print(".get(");
        visit(node.exprNode);
        writer.print(")");
    }

    @Override
    public void visit(FuncCallNode node) {
        visit(node.idNode);
        writer.print("(");
        visit(node.callCntNode);
        writer.print(");\n");
    }

    @Override
    public void visit(CallCntNode node) {
        for (int i = 0; i < node.exprNodes.size(); i++) {
            if (i != 0) {
                writer.print(", ");
            }
            visit(node.exprNodes.get(i));
        }
    }

    @Override
    public void visit(IncludeNode node) {
        writer.print("#include \"");
        visit(node.idNode);
        writer.print("/");
        visit(node.idNode);
        writer.print(".h\"\n");
    }

    @Override
    public void visit(AdvTypeModifierNode node) {

        for (ExprNode expr : node.exprNodes) {
            visit(node.idNode);
            if (node.advancedTypeModifierOperator == AdvancedTypeModifierOperator.ADD) {
                writer.print(".add(");
            }

            if (node.advancedTypeModifierOperator == AdvancedTypeModifierOperator.REMOVE) {
                writer.print(".remove(");
            }
            visit(expr);
            writer.print(");\n");
        }
    }

    // TODO Tjek om ting er call by reference

}
 