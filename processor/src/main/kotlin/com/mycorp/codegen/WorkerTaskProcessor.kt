package com.mycorp.codegen

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.mycorp.dsl.WorkerTask
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*
import kotlin.reflect.KClass

class WorkerTaskProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // 1. Find all classes with our annotation
        val symbols = resolver.getSymbolsWithAnnotation(WorkerTask::class.qualifiedName!!)

        symbols.filterIsInstance<KSClassDeclaration>().forEach { workerClass ->
            // 2. Validate and analyze the class
            val workerInterface = findWorkerInterface(workerClass)
            if (workerInterface == null) {
                logger.error("The @WorkflowTask annotation must be on a class that implements the Worker interface.", workerClass)
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
            .firstOrNull { (it.declaration as? KSClassDeclaration)?.qualifiedName?.asString() == "com.mycorp.dsl.Worker" }
    }

    private fun generateDescriptor(workerClass: KSClassDeclaration, workerInterface: KSType) {
        val packageName = workerClass.packageName.asString()
        val workerClassName = workerClass.simpleName.asString()
        val descriptorName = "${workerClassName}_Descriptor"

        val annotation: KSAnnotation = workerClass.annotations.first {
            it.shortName.asString() == WorkerTask::class.simpleName
        }

        val nameArgument = annotation.arguments.find { it.name?.asString() == "name" }
        val providedName = nameArgument?.value as? String ?: ""
        val finalTaskName = if (providedName.isNotBlank()) providedName else workerClassName

        val descriptionArgument = annotation.arguments.find { it.name?.asString() == "description" }
        val description = descriptionArgument?.value as? String ?: ""


        // 4. Extract all the necessary information
        val inputType = workerInterface.arguments[0].type!!.resolve().toClassName()
        val outputType = workerInterface.arguments[1].type!!.resolve().toClassName()

        // ... get annotation values for name, retries, etc. ...

        // 5. Build the file using KotlinPoet
        val fileSpec = FileSpec.builder(packageName, descriptorName)
            .addType(TypeSpec.objectBuilder(descriptorName)
                .addSuperinterface(ClassName("com.mycorp.dsl", "WorkerDescriptor"))
                // Add properties like taskName, inputType, outputType, etc.
                .addProperty(
                    PropertySpec.builder("name", String::class, KModifier.OVERRIDE)
                        .initializer("%S", finalTaskName)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder(
                        "workerType",
                        KClass::class.asTypeName().parameterizedBy(workerClass.toClassName()),
                        KModifier.OVERRIDE
                    )
                        .initializer("%T::class", workerClass.toClassName()) // %T formats the value as a Type
                        .build()
                )
                .addProperty(PropertySpec.builder("inputType", KClass::class.asTypeName().parameterizedBy(STAR))
                    .initializer("%T::class", inputType)
                    .addModifiers(KModifier.OVERRIDE)
                    .build())
                .addProperty(PropertySpec.builder("outputType", KClass::class.asTypeName().parameterizedBy(STAR))
                    .initializer("%T::class", outputType)
                    .addModifiers(KModifier.OVERRIDE)
                    .build())
                .build())
            .build()

        // 6. Write the new file to disk
        fileSpec.writeTo(codeGenerator, dependencies = Dependencies(true, workerClass.containingFile!!))
    }
}