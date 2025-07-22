package com.meeting.demo.domain.vo

data class GitHubRepository(val value: String) {
    init {
        require(isValid(value)) { "Invalid GitHub repository URL: $value" }
    }

    companion object {
        private const val GITHUB_REPO_REGEX = "^(https://)?(www\\.)?github\\.com/[A-Za-z0-9_-]+/[A-Za-z0-9_-]+/?\$"

        fun isValid(repoUrl: String): Boolean {
            return repoUrl.matches(Regex(GITHUB_REPO_REGEX))
        }

        fun createOrNull(repoUrl: String?): GitHubRepository? {
            return if (repoUrl.isNullOrBlank() || !isValid(repoUrl)) {
                null
            } else {
                GitHubRepository(repoUrl)
            }
        }
    }

    override fun toString(): String = value
}