package com.example.data.multitrack.command

import com.example.data.multitrack.MultiTrackComposition

/**
 * Gestor de historial de comandos para la arquitectura multi-pista.
 * Administra las pilas de Deshacer (Undo) y Rehacer (Redo), garantizando
 * transiciones atómicas, límite de memoria configurable y consulta de telemetría editorial.
 */
class MultiTrackHistoryManager(
    val maxHistorySize: Int = 50
) {
    private val undoStack = mutableListOf<MultiTrackCommand>()
    private val redoStack = mutableListOf<MultiTrackCommand>()

    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    val undoCount: Int
        get() = undoStack.size

    val redoCount: Int
        get() = redoStack.size

    /**
     * Título de la acción más reciente que se deshará si se pulsa Undo.
     */
    val lastActionTitle: String?
        get() = undoStack.lastOrNull()?.title

    /**
     * Título de la acción que se rehará si se pulsa Redo.
     */
    val nextRedoTitle: String?
        get() = redoStack.lastOrNull()?.title

    /**
     * Lista completa de acciones en la pila de deshacer en orden cronológico inverso (el más reciente primero).
     */
    val undoHistoryTitles: List<String>
        get() = undoStack.map { it.title }.reversed()

    /**
     * Lista completa de acciones en la pila de rehacer en orden de ejecución siguiente.
     */
    val redoHistoryTitles: List<String>
        get() = redoStack.map { it.title }.reversed()

    /**
     * Ejecuta un comando sobre la composición dada, añade el comando a la pila de Undo
     * y vacía la pila de Redo.
     */
    @Synchronized
    fun execute(
        command: MultiTrackCommand,
        current: MultiTrackComposition
    ): MultiTrackComposition {
        val nextComposition = command.execute(current)

        undoStack.add(command)
        if (undoStack.size > maxHistorySize) {
            undoStack.removeAt(0)
        }

        redoStack.clear()
        return nextComposition
    }

    /**
     * Deshace la última acción editorial aplicada.
     * Retorna el nuevo estado y el comando deshecho, o null si la pila está vacía.
     */
    @Synchronized
    fun undo(current: MultiTrackComposition): Pair<MultiTrackComposition, MultiTrackCommand>? {
        if (undoStack.isEmpty()) return null
        val command = undoStack.removeAt(undoStack.lastIndex)
        val revertedComposition = command.undo(current)
        redoStack.add(command)
        return Pair(revertedComposition, command)
    }

    /**
     * Rehace la última acción editorial deshecha.
     * Retorna el nuevo estado y el comando rehecho, o null si la pila está vacía.
     */
    @Synchronized
    fun redo(current: MultiTrackComposition): Pair<MultiTrackComposition, MultiTrackCommand>? {
        if (redoStack.isEmpty()) return null
        val command = redoStack.removeAt(redoStack.lastIndex)
        val reappliedComposition = command.execute(current)
        undoStack.add(command)
        return Pair(reappliedComposition, command)
    }

    /**
     * Restablece completamente el historial de deshacer y rehacer.
     */
    @Synchronized
    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
