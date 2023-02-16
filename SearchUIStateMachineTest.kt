package com.bits.bytes.presentation

import app.cash.turbine.test
import com.bits.bytes.domain.SearchUseCase
import com.bits.bytes.domain.model.AssetType
import com.bits.bytes.domain.model.DomainPublicAsset
import com.bits.bytes.presentation.model.toDisplayPublicModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID
import kotlin.random.Random
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SearchUIStateMachineTest {

    @Test
    fun `should switch to a searching state after receiving a search action while a current state is a on search results state`() =
        runTest {
            val fakeKeyword = UUID.randomUUID().toString()
            val fakePublicAssets = generateFakePublicAsset()

            val mockUseCase = mockk<SearchUseCase>()
            coEvery { mockUseCase.execute(keyword = fakeKeyword) } returns fakePublicAssets

            val sut = SearchUIStateMachine(searchUseCase = mockUseCase)
            sut.state.test {
                assertEquals(
                    expected = SearchUIState.OnSearchResults(publicAssets = emptyList()),
                    actual = awaitItem()
                )
                sut.dispatch(SearchAction.Search(keyword = fakeKeyword))
                assertEquals(
                    expected = SearchUIState.Searching(keyword = fakeKeyword),
                    actual = awaitItem()
                )
                assertEquals(
                    expected = SearchUIState.OnSearchResults(fakePublicAssets
                        .filter { it.type != AssetType.Unsupported }
                        .sortedBy { it.type.sortOrder }
                        .map { it.toDisplayPublicModel() }),
                    actual = awaitItem()
                )
            }

            coVerify(exactly = 1) { mockUseCase.execute(keyword = fakeKeyword) }
        }

    @Test
    fun `should switch to a error state when a use case throws an exception and then switch back to the initial state`() = runTest {
        val fakeKeyword = UUID.randomUUID().toString()
        val fakeError = Exception("error")

        val mockUseCase = mockk<SearchUseCase>()
        coEvery { mockUseCase.execute(keyword = fakeKeyword) }.throws(fakeError)

        val sut = SearchUIStateMachine(searchUseCase = mockUseCase)
        sut.state.test {
            assertEquals(
                expected = SearchUIState.OnSearchResults(publicAssets = emptyList()),
                actual = awaitItem()
            )
            sut.dispatch(SearchAction.Search(keyword = fakeKeyword))
            assertEquals(
                expected = SearchUIState.Searching(keyword = fakeKeyword),
                actual = awaitItem()
            )
            assertEquals(
                expected = SearchUIState.OnError(e = fakeError),
                actual = awaitItem()
            )
            assertEquals(
                expected = SearchUIState.OnSearchResults(publicAssets = emptyList()),
                actual = awaitItem()
            )
        }

        coVerify(exactly = 1) { mockUseCase.execute(keyword = fakeKeyword) }
    }

    private val randomType: () -> AssetType = {
        when (Random.nextInt(0, 4)) {
            0 -> AssetType.Crypto
            1 -> AssetType.Cash
            2 -> AssetType.Stock
            else -> AssetType.Unsupported
        }
    }

    private fun generateFakePublicAsset() =
        mutableListOf<DomainPublicAsset>().apply {
            repeat((1..100).count()) {
                add(
                    DomainPublicAsset(
                        publicId = Random.nextString(),
                        type = randomType(),
                        symbol = Random.nextString(),
                        name = Random.nextString()
                    )
                )
            }
        }.toList()
}