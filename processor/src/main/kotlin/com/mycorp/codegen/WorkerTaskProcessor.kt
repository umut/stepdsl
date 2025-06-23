package com.mycorp.codegen

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.mycorp.dsl.Worker
import com.mycorp.dsl.WorkerTask
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*
import kotlin.reflect.KClass

class WorkerTaskProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    val names = mutableSetOf<String>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // 1. Find all classes with WorkerTask annotation
        val symbols = resolver.getSymbolsWithAnnotation(WorkerTask::class.qualifiedName!!)

        symbols.filterIsInstance<KSClassDeclaration>().forEach { workerClass ->
            // 2. Validate and analyze the class
            val workerInterface = findWorkerInterface(workerClass)
            println(workerInterface)
            if (workerInterface == null) {
                logger.error(
                    """
                    The @${WorkerTask::class.simpleName} annotation must be on a class that implements the 
                    ${Worker::class.simpleName} interface.
                    """, workerClass
                )
                return@forEach
            }

            // 3. Generate the new source file
            generateDescriptor(workerClass, workerInterface)
        }
        return emptyList()
    }

    private fun findWorkerInterface(workerClass: KSClassDeclaration): KSType? {
        // This logic finds the Worker<I, O> interface
        return workerClass.superTypes.map { it.resolve() }
            .firstOrNull {
                (it.declaration as? KSClassDeclaration)?.qualifiedName?.asString() == Worker::class.qualifiedName
            }
    }

    private fun generateDescriptor(workerClass: KSClassDeclaration, workerInterface: KSType) {
        val workerPackageName = workerClass.packageName.asString()
        val workerClassName = workerClass.simpleName.asString()
        val workerDescriptorName = "${workerClassName}Descriptor"

        val annotation: KSAnnotation = workerClass.annotations.first {
            it.shortName.asString() == WorkerTask::class.simpleName
        }

        val nameArgument = annotation.arguments.find { it.name?.asString() == "name" }
        val nameValue = nameArgument?.value as? String ?: ""
        val nameFinalValue = nameValue.ifBlank { workerClassName }
        if (names.contains(nameFinalValue)) {
            throw IllegalStateException("Name $nameFinalValue is used for nmore than one Worker.")
        } else {
            names.add(nameFinalValue)
        }

        val descriptionArgument = annotation.arguments.find { it.name?.asString() == "description" }
        val descriptionFinalValue = descriptionArgument?.value as? String ?: ""

        val inputType = workerInterface.arguments[0].type!!.resolve().toClassName()
        val outputType = workerInterface.arguments[1].type!!.resolve().toClassName()

        val fileSpec = FileSpec.builder(workerPackageName, workerDescriptorName)
            .addType(
                TypeSpec.objectBuilder(workerDescriptorName)
                    .addSuperinterface(
                        ClassName("com.mycorp.dsl", "WorkerDescriptor")
                            .parameterizedBy(inputType, outputType)
                    )
                    .addProperty(
                        PropertySpec.builder("name", String::class, KModifier.OVERRIDE)
                            .initializer("%S", nameFinalValue)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("description", String::class, KModifier.OVERRIDE)
                            .initializer("%S", descriptionFinalValue)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            "type",
                            KClass::class.asTypeName().parameterizedBy(workerClass.toClassName())
                        )
                            .initializer("%T::class", workerClass.toClassName())
                            .addModifiers(KModifier.OVERRIDE)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("inputType", KClass::class.asTypeName().parameterizedBy(STAR))
                            .initializer("%T::class", inputType)
                            .addModifiers(KModifier.OVERRIDE)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("outputType", KClass::class.asTypeName().parameterizedBy(STAR))
                            .initializer("%T::class", outputType)
                            .addModifiers(KModifier.OVERRIDE)
                            .build()
                    )
                    .build()
            )
            .build()

        fileSpec.writeTo(codeGenerator, dependencies = Dependencies(true, workerClass.containingFile!!))
    }
}