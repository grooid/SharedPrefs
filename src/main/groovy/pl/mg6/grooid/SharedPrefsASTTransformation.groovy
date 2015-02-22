package pl.mg6.grooid

import android.content.SharedPreferences
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import pl.mg6.utils.StringUtils

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class SharedPrefsASTTransformation extends AbstractASTTransformation {

    private static final String sharedPrefsFieldName = "__sharedPrefs"
    private static final String constructorSharedPrefsParamName = "sharedPrefs"

    private ClassNode classNode

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        this.classNode = nodes[1] as ClassNode
        addConstructor()
        addMethods()
        addFields()
    }

    private void addFields() {
        def type = ClassHelper.make(SharedPreferences)
        classNode.addField(sharedPrefsFieldName, ACC_PRIVATE | ACC_FINAL, type, null)
    }

    private void addConstructor() {
        new AstBuilder().buildFromSpec {
            constructor(ACC_PUBLIC) {
                // MyPrefs(SharedPreferences sharedPrefs) {
                //     __sharedPrefs = sharedPrefs
                // }
                parameters {
                    parameter "$constructorSharedPrefsParamName": SharedPreferences
                }
                exceptions {}
                block {
                    expression {
                        binary {
                            variable sharedPrefsFieldName
                            token "="
                            variable constructorSharedPrefsParamName
                        }

                    }
                }
            }
        }.each { constructor ->
            classNode.addConstructor constructor
        }
    }

    private void addMethods() {
        classNode.fields.each { field ->
            def methodName = field.name.capitalize()
            def prefName = StringUtils.toLowercaseWithUnderscores(field.name)
            new AstBuilder().buildFromSpec {
                method("get$methodName", ACC_PUBLIC, sharedPrefsTypeForField(field)) {
                    // Xxx getMyField() {
                    //     return __sharedPrefs.getXxx("my_field", myField)
                    // }
                    parameters {}
                    exceptions {}
                    block {
                        returnStatement {
                            methodCall {
                                variable sharedPrefsFieldName
                                constant "get" + sharedPrefsNameForField(field)
                                argumentList {
                                    constant prefName
                                    variable field.name
                                }
                            }
                        }
                    }
                }
                method("set$methodName", ACC_PUBLIC, void) {
                    // void setMyField(Xxx myField) {
                    //     __sharedPrefs.edit().putXxx("my_field", myField).apply()
                    // }
                    parameters {
                        parameter "$field.name": sharedPrefsTypeForField(field)
                    }
                    exceptions {}
                    block {
                        expression {
                            methodCall {
                                methodCall {
                                    methodCall {
                                        variable sharedPrefsFieldName
                                        constant "edit"
                                        argumentList {}
                                    }
                                    constant "put" + sharedPrefsNameForField(field)
                                    argumentList {
                                        constant prefName
                                        variable field.name
                                    }
                                }
                                constant "apply"
                                argumentList {}
                            }
                        }
                    }
                }
                method("contains$methodName", ACC_PUBLIC, boolean) {
                    // boolean MyField() {
                    //     return __sharedPrefs.contains("my_field")
                    // }
                    parameters {}
                    exceptions {}
                    block {
                        returnStatement {
                            methodCall {
                                variable sharedPrefsFieldName
                                constant "contains"
                                argumentList {
                                    constant prefName
                                }
                            }
                        }
                    }
                }
                method("remove$methodName", ACC_PUBLIC, void) {
                    // void removeMyField() {
                    //     __sharedPrefs.edit().remove("my_field").apply()
                    // }
                    parameters {}
                    exceptions {}
                    block {
                        expression {
                            methodCall {
                                methodCall {
                                    methodCall {
                                        variable sharedPrefsFieldName
                                        constant "edit"
                                        argumentList {}
                                    }
                                    constant "remove"
                                    argumentList {
                                        constant prefName
                                    }
                                }
                                constant "apply"
                                argumentList {}
                            }
                        }
                    }
                }
            }.each { method ->
                classNode.addMethod method
            }
        }
    }

    private String sharedPrefsNameForField(FieldNode field) {
        switch (field.type.typeClass) {
            case int:
                return "Int"
            case float:
                return "Float"
            case long:
                return "Long"
            default:
                return "String"
        }
    }

    private Class<?> sharedPrefsTypeForField(FieldNode field) {
        switch (field.type.typeClass) {
            case int:
                return int
            case float:
                return float
            case long:
                return long
            default:
                return String
        }
    }
}
