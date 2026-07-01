package com.imagenim.app.data

data class GenerateRequest(
    val prompt: String,
    val negative_prompt: String = "",
    val cfg_scale: Int = 5,
    val steps: Int = 25,
    val seed: Int = 0,
    val width: Int = 1024,
    val height: Int = 1024
)

data class EditRequest(
    val prompt: String,
    val image: String,
    val mode: String? = null,
    val negative_prompt: String? = null,
    val cfg_scale: Int? = null,
    val steps: Int? = null,
    val seed: Int? = null,
    val width: Int? = null,
    val height: Int? = null
)

data class NIMResponse(
    val artifacts: List<Artifact>?
)

data class Artifact(
    val base64: String?,
    val seed: Long?,
    val finish_reason: String?
)

data class ModelInfo(
    val displayName: String,
    val publisher: String,
    val modelName: String
)

object ModelCatalog {
    val generationModels = listOf(
        ModelInfo("FLUX.1-dev", "black-forest-labs", "flux.1-dev"),
        ModelInfo("FLUX.1-schnell", "black-forest-labs", "flux.1-schnell"),
        ModelInfo("Stable Diffusion 3.5 Large", "stabilityai", "stable-diffusion-3.5-large"),
        ModelInfo("Qwen-Image", "qwen", "qwen-image"),
        ModelInfo("FLUX.2-klein-4B", "black-forest-labs", "flux.2-klein-4b")
    )

    val editingModels = listOf(
        ModelInfo("FLUX.1-dev", "black-forest-labs", "flux.1-dev"),
        ModelInfo("FLUX.1-Kontext-dev", "black-forest-labs", "flux.1-kontext-dev"),
        ModelInfo("Stable Diffusion 3.5 Large", "stabilityai", "stable-diffusion-3.5-large"),
        ModelInfo("Qwen-Image-Edit", "qwen", "qwen-image-edit"),
        ModelInfo("FLUX.2-klein-4B", "black-forest-labs", "flux.2-klein-4b")
    )
}
