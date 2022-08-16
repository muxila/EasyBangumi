package com.heyanle.easybangumi.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.heyanle.easybangumi.entity.Bangumi
import java.lang.Exception
import java.lang.NullPointerException

/**
 * Created by HeYanLe on 2021/10/20 20:47.
 * https://github.com/heyanLE
 */
class SearchPageSource(
    private val searchParser: ISearchParser,
    private val keyword: String,
) : PagingSource<Int, Bangumi>() {

    override fun getRefreshKey(state: PagingState<Int, Bangumi>): Int {
        return searchParser.firstKey()
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bangumi> {
        val key = params.key ?: return LoadResult.Error(NullPointerException())
        if(keyword.isEmpty()){
            return LoadResult.Error(NullPointerException())
        }
        try{
            searchParser.search(keyword, key).let {
                return when(it){
                    is ISourceParser.ParserResult.Error -> {
                        if(it.isParserError){
                            LoadResult.Error(it.throwable)
                        }else{
                            LoadResult.Error(Exception("load error"))
                        }

                    }
                    is ISourceParser.ParserResult.Complete -> {
                        LoadResult.Page(
                            data = it.data.second,
                            prevKey = null,
                            nextKey = it.data.first
                        )
                    }
                }
            }
        }catch (e: Exception){
            return LoadResult.Error(e)
        }

    }
}