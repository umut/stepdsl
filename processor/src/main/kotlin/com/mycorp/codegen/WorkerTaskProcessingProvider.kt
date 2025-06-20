package com.mycorp.codegen
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class WorkerTaskProcessingProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return WorkerTaskProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}