package org.secfirst.umbrella.whitelabel.serialize

abstract class PathUtils {
    companion object {
        fun getLastDirectory(path: String): String {
            val splitPath = path.split("/").filter { it.isNotEmpty() }
            return splitPath[splitPath.lastIndex]
        }

        private fun getSplitPath(path: String) = path.split("/").filter { it.isNotEmpty() }

        fun getLevelOfPath(path: String) = getSplitPath(path).size

        fun getWorkDirectory(path: String): String {
            val splitPath = getSplitPath(path)
            var pwd = ""
            for (i in 0 until splitPath.size - 1)
                pwd += splitPath[i] + "/"
            return pwd
        }
    }
}