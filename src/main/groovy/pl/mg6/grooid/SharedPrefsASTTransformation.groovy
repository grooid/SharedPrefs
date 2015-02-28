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
            if (supportedType(field)) {
                new AstBuilder().buildFromSpec {
                    method("get$methodName", ACC_PUBLIC, field.type.typeClass) {
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
                            parameter "$field.name": field.type.typeClass
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
                }.each { method ->
                    classNode.addMethod method
                }
            } else if (field.type.typeClass == double) {
                new AstBuilder().buildFromSpec {
                    method("get$methodName", ACC_PUBLIC, double) {
                        // double getMyField() {
                        //     return Double.longBitsToDouble(__sharedPrefs.getLong("my_field", Double.doubleToLongBits(myField)))
                        // }
                        parameters {}
                        exceptions {}
                        block {
                            returnStatement {
                                methodCall {
                                    classExpression Double
                                    constant "longBitsToDouble"
                                    argumentList {
                                        methodCall {
                                            variable sharedPrefsFieldName
                                            constant "getLong"
                                            argumentList {
                                                constant prefName
                                                methodCall {
                                                    classExpression Double
                                                    constant "doubleToLongBits"
                                                    argumentList {
                                                        variable field.name
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    method("set$methodName", ACC_PUBLIC, void) {
                        // void setMyField(double myField) {
                        //     __sharedPrefs.edit().putLong("my_field", Double.doubleToLongBits(myField)).apply()
                        // }
                        parameters {
                            parameter "$field.name": double
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
                                        constant "putLong"
                                        argumentList {
                                            constant prefName
                                            methodCall {
                                                classExpression Double
                                                constant "doubleToLongBits"
                                                argumentList {
                                                    variable field.name
                                                }
                                            }
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
            new AstBuilder().buildFromSpec {
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
        return field.type.typeClass.simpleName.capitalize()
    }

    private boolean supportedType(FieldNode field) {
        Class<?> clazz = field.type.typeClass
        return clazz == boolean || clazz == float || clazz == int || clazz == long || clazz == String
    }
}
