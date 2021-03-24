package com.mojtaba;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "com.mojtaba.MySingleton"
})

public final class MyProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment)
    {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(MySingleton.class))
        {
            if (annotatedElement.getKind() != ElementKind.CLASS)
            {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        String.format("Only classes can be annotated with @" + MySingleton.class.getSimpleName()));
                return true;
            }

            try
            {
                generateJavaFile(annotatedElement, filer);
            }
            catch (IOException ex)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.toString());
            }
        }

        return true;
    }

    public void generateJavaFile(Element annotatedElement, Filer filer) throws IOException
    {
        String PACKAGE_NAME = "com.mojtaba";
        String className = annotatedElement.getSimpleName().toString();

        String fileName = className + "_singleton";

        List<Element> originatingElements = new ArrayList<>();

        JavaFileObject filerSourceFile = filer.createSourceFile(fileName,
                originatingElements.toArray(new Element[originatingElements.size()]));

        try (Writer writer = filerSourceFile.openWriter())
        {

            writer.append(
                    // imports
                    "package " + PACKAGE_NAME + ";" +
                            "\n\n" +

                            // class declaration
                            "public class " + fileName + " {\n " +

                            //static field
                            "\tprivate static " + className + " INSTANCE;\n\n" +

                            // method body
                            "\tpublic static " + className + " getInstance() \n\t{\n" +
                            "\t\tif (INSTANCE == null) \n" +
                            "\t\t{\n" +
                            "\t\t\tINSTANCE = new " + className + "();\n" +
                            "\t\t}\n" +
                            "\n" +
                            "\t\treturn INSTANCE;\n" +
                            "\t}\n" +

                            "}");

        }
        catch (Exception e)
        {
            try
            {
                filerSourceFile.delete();
            }
            catch (Exception ignored)
            {
            }
            throw e;
        }
    }
}
