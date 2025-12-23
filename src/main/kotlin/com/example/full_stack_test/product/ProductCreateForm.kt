package com.example.full_stack_test.product

import jakarta.validation.constraints.NotBlank

data class ProductCreateForm(

    @field:NotBlank
    val title: String,

    val vendor: String?,
    val productType: String?,

    val variants: List<VariantCreateForm> = emptyList()
)