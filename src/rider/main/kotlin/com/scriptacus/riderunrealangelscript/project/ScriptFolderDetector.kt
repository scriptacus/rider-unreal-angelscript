package com.scriptacus.riderunrealangelscript.project

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

/**
 * Shared utility for efficiently detecting AngelScript Script folders in a project.
 *
 * Uses FilenameIndex for O(1) lookup of .uproject and .uplugin files instead of
 * recursive VFS traversal which is O(n) where n = total files in project.
 */
object ScriptFolderDetector {
    /**
     * Finds all Script folders next to .uproject or .uplugin files.
     *
     * @param project The project to search
     * @return List of Script folder VirtualFiles
     */
    fun detectScriptFolders(project: Project): List<VirtualFile> {
        val scriptFolders = mutableSetOf<VirtualFile>()
        val scope = GlobalSearchScope.projectScope(project)

        // Find all .uproject files and check for Script folders
        val uprojectFiles = FilenameIndex.getAllFilesByExt(project, "uproject", scope)
        for (uprojectFile in uprojectFiles) {
            val scriptFolder = uprojectFile.parent?.findChild("Script")
            if (scriptFolder?.isDirectory == true) {
                scriptFolders.add(scriptFolder)
            }
        }

        // Find all .uplugin files and check for Script folders
        val upluginFiles = FilenameIndex.getAllFilesByExt(project, "uplugin", scope)
        for (upluginFile in upluginFiles) {
            val scriptFolder = upluginFile.parent?.findChild("Script")
            if (scriptFolder?.isDirectory == true) {
                scriptFolders.add(scriptFolder)
            }
        }

        return scriptFolders.toList()
    }
}