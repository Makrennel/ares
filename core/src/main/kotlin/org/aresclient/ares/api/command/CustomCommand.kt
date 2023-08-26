package org.aresclient.ares.api.command

import org.aresclient.ares.api.Ares
import org.aresclient.ares.api.setting.Setting
import java.util.LinkedList

/**
 * Abstract class from which all Ares commands are derived
 */
abstract class CustomCommand(name: String, val description: String, val help: LinkedList<String>, vararg alternativeNames: String) {
    companion object {
        private val COMMANDS = hashMapOf<String, CustomCommand>()
        private val ALTERNATIVE_COMMANDS = hashMapOf<String, String>()

        private val PREFIX = Ares.getSettings().addString("Command Prefix", "-")

        /** Processes a command given to the console, or following the specified prefix in the chat */
        fun processCommand(command: String) {
            val parts = toParts(command)
            if (parts == null) {
                outputText("NO COMMAND SPECIFIED")
                return
            }

            var current = parts.poll().lowercase()

            if (!COMMANDS.containsKey(current) && !ALTERNATIVE_COMMANDS.containsKey(current)) {
                outputText("COMMAND NOT FOUND")
                return
            }

            if (ALTERNATIVE_COMMANDS.containsKey(current)) current = ALTERNATIVE_COMMANDS[current]!!

            COMMANDS[current]?.execute(parts)
        }

        /** Returns a list of commands based on what is currently in the console, or following a prefix in a chat */
        fun tabComplete(currentCommand: String): LinkedList<String> {
            // Return all command names if none specified at all
            val parts = toParts(currentCommand) ?: return LinkedList(COMMANDS.keys)
            val current = parts.poll().lowercase()

            // Defer to command specific completion
            if (ALTERNATIVE_COMMANDS.containsKey(current)) return COMMANDS[ALTERNATIVE_COMMANDS[current]]!!.tabComplete(parts)
            if (COMMANDS.containsKey(current)) return COMMANDS[current]!!.tabComplete(parts)

            // If no command yet fully specified, return all command names possible from the given starting string
            val possibilities = LinkedList<String>()
            COMMANDS.keys.forEach { if (it.startsWith(current)) possibilities.add(it) }
            return possibilities
        }

        /** Provides a one word hinting of what expected in the current part of the command being typed */
        fun hint(currentCommand: String): String {
            val parts = toParts(currentCommand) ?: return "command"
            val current = parts.poll().lowercase()

            // Defer to command specific hints
            if (ALTERNATIVE_COMMANDS.containsKey(current)) return COMMANDS[ALTERNATIVE_COMMANDS[current]]!!.hintNextPart(parts)
            if (COMMANDS.containsKey(current)) return COMMANDS[current]!!.hintNextPart(parts)

            // TODO: something better here - maybe defer to tab completion possibilities?
            if (parts.isEmpty()) return "command"

            // No real command typed but has multiple parts, therefore confused
            return "???"
        }

        protected fun <T> Collection<T>.toLinkedList(): LinkedList<T> {
            return LinkedList(this)
        }

        /** Output Text to the given interface - whether than be a rendered console, or the chat */
        protected fun outputText(text: String) {
            // TODO: Turn console pritns into ingame visible chat messages or some rendered alternative
            println(text)
        }

        /** Processes a provided path to get the setting specified */
        protected fun processPath(path: String): Setting<*>? {
            // Split the path into individual names
            val parts = path.split('/').toLinkedList()

            // Replace underscores with spaces - paths are given with underscores where spaces are found in the name
            for ((index, s) in parts.withIndex()) {
                parts[index] = s.replace('_',' ')
            }

            // Get to the bottom of the given path, or return null if impossible
            var current: Setting.Map<*>? = Ares.getSettings()
            while (parts.size != 0) {
                if (current !is Setting.Map<*>) {
                    if (current != null) outputText("${current.name} IS NOT A PATH")
                    if (current == null) outputText("NOT A VALID PATH")
                    return null
                }
                current = nextSetting(current, parts.poll())
            }

            if (current == null) outputText("NOT A VALID PATH")
            return current
        }

        /** Gets the next setting from the last setting map */
        private fun nextSetting(last: Setting.Map<*>, next: String): Setting.Map<*>? {
            // Search for the correct key and return its value
            for (entry in last.value.entries) {
                if (entry.key.contentEquals(next, true)) {
                    return entry.value as Setting.Map<*>
                }
            }
            return null
        }

        /** Split the full command provided into its parts as a queue so that it can be processed sequentially */
        private fun toParts(fullCommand: String): LinkedList<String>? {
            // Split string where spaces separate words
            val parts = fullCommand.split(' ').toLinkedList()
            parts.removeIf { it.isEmpty() }

            // Remove prefix
            if (parts[0].length == PREFIX.value.length) {
                if (parts.size == 1) return null
                parts.removeFirst()
            }
            else parts[0] = parts[0].substring(PREFIX.value.length)

            return parts
        }
    }

    init {
        COMMANDS[name] = this
        for (name in alternativeNames) ALTERNATIVE_COMMANDS[name] = name
    }

    /** Processes and does what the command requests */
    abstract fun execute(command: LinkedList<String>)

    /** Handles command specific tab completions */
    abstract fun tabComplete(command: LinkedList<String>): LinkedList<String>

    /** Provides a one word hinting of what expected in the current part of the command being typed */
    abstract fun hintNextPart(command: LinkedList<String>): String

    /** Provides an ordered list of strings with helpful information */
    abstract fun help(): LinkedList<String>

    abstract class Part(val command: LinkedList<String>, val hint: String) {
        val part = command.poll()

        abstract fun execute()
    }
}