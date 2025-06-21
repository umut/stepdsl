package com.mycorp.workflow.util

import com.mycorp.dsl.Worker
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

object GenericsUtils {
    /**
     * This implementation uses standard Java reflection to find the generic types.
     * It is more resilient to Kotlin versioning and classpath issues than the
     * Kotlin-specific reflection API.
     *
     * @return A Pair containing (InputKClass, OutputKClass)
     */
    fun getWorkerIOTypes(workerClass: KClass<out Worker<*, *>>): Pair<KClass<*>, KClass<*>> {
        // Use the underlying Java Class object which is always available
        val javaClass = workerClass.java

        // Find the Worker interface from the list of generic interfaces
        val workerInterface = javaClass.genericInterfaces.find {
            it is ParameterizedType && it.rawType == Worker::class.java
        } as? ParameterizedType ?: return Any::class to Any::class // Return default if not found

        // Get the <I, O> type arguments from the ParameterizedType
        val typeArgs = workerInterface.actualTypeArguments
        if (typeArgs.size == 2) {
            // Convert the Java `Type` back to a Kotlin `KClass`
            val inputType = (typeArgs[0] as Class<*>).kotlin
            val outputType = (typeArgs[1] as Class<*>).kotlin
            return inputType to outputType
        }

        return Any::class to Any::class
    }
}