package com.dn0ne.player.app.data.remote.metadata

import com.dn0ne.player.app.domain.metadata.MetadataSearchResult
import com.dn0ne.player.app.domain.result.DataError
import com.dn0ne.player.app.domain.result.Result

interface MetadataProvider {
    suspend fun searchMetadata(query: String, trackDuration: Long): Result<List<MetadataSearchResult>, DataError>
    suspend fun getCoverArtBytes(searchResult: MetadataSearchResult): Result<ByteArray, DataError>
}