package pion.tech.pionbase.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.home.domain.model.TemplateData
import pion.tech.pionbase.home.domain.repository.ApiRepository

class TemplateUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(categoryId: String): Flow<Result<List<TemplateData>>> {
        return repository.getTemplateData(categoryId = categoryId)
    }
}