package com.example.full_stack_test.product

import jakarta.validation.constraints.NotBlank

data class ProductCreateForm(

    @field:NotBlank
    val title: String,

    @field:NotBlank
    val vendor: String,

    @field:NotBlank
    val productType: String,

    val variants: List<VariantCreateForm> = emptyList()
)