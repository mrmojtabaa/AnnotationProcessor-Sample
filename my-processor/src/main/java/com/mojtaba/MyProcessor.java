package com.mojtaba;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
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

    private int round = -1;

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
        round++;
        Set<? extends Element> elementsAnnotatedWithMySingleton = roundEnvironment.getElementsAnnotatedWith(MySingleton.class);
        for (Element annotatedElement : elementsAnnotatedWithMySingleton)
        {
            if (annotatedElement.getKind() != ElementKind.CLASS)
            {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        String.format("Only classes can be annotated with @" + MySingleton.class.getSimpleName()));
                return true;
            }
        }

        try
        {
            if (round == 0)
            {
                generateJavaFile(elementsAnnotatedWithMySingleton, filer);
            }
        }
        catch (IOException ex)
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.toString());
        }

        return true;
    }

    public void generateJavaFile(Collection<? extends Element> elements, Filer filer) throws IOException
    {
        String PACKAGE_NAME = "com.mojtaba";

        String fileName = "AllSingletons";

        List<Element> originatingElements = new ArrayList<>();

        JavaFileObject filerSourceFile = filer.createSourceFile(fileName,
                originatingElements.toArray(new Element[originatingElements.size()]));

        try (Writer writer = filerSourceFile.openWriter())
        {
            writer.append(
                    // imports
                    "package " + PACKAGE_NAME + ";" +
                            "\n\n");

            writer.append(
                    // class declaration
                    "public class " + fileName + " {\n ");

            for (Element element : elements)
            {
                String className = element.getSimpleName().toString();

                writer.append(
                        //static field
                        "\tprivate static " + className + " " + className + "_INSTANCE;\n\n" +

                                // method body
                                "\tpublic static " + className + " get" + className + "Instance() \n\t{\n" +
                                "\t\tif (" + className + "_INSTANCE == null) \n" +
                                "\t\t{\n" +
                                "\t\t\t" + className + "_INSTANCE = new " + className + "();\n" +
                                "\t\t}\n" +
                                "\n" +
                                "\t\treturn " + className + "_INSTANCE;\n" +
                                "\t}\n");

                writer.append("\n\n");
            }

            writer.append("}");
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
