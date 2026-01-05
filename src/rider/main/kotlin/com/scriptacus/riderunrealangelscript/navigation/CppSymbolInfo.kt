package com.scriptacus.riderunrealangelscript.navigation

/**
 * Represents C++ symbol information resolved from an AngelScript symbol.
 *
 * @property className The C++ class name (e.g., "AActor"), or empty string for global symbols
 * @property symbolName The C++ symbol name (e.g., "GetActorLocation")
 */
data class CppSymbolInfo(
    val className: String,
    val symbolName: String
)