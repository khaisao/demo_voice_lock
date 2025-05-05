package pion.tech.pionbase.home.domain.usecase

import pion.tech.pionbase.home.domain.model.AppCategoryData
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.home.domain.repository.ApiRepository

class AppCategoryUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(): Flow<Result<List<AppCategoryData>>> {
        return repository.getAppCategory()
    }
}