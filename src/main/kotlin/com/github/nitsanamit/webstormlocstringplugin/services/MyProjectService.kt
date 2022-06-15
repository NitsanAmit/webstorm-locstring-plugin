package com.github.nitsanamit.webstormlocstringplugin.services

import com.intellij.openapi.project.Project
import com.github.nitsanamit.webstormlocstringplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
