package com.sadturtleman.androidsampleproject.detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * TODO(design): 시안 구현은 [com.sadturtleman.androidsampleproject.detail.presentation.detail.DetailView] 에 있다.
 *  ViewModel 배선이 끝나면 이 파일을 DetailView 호출로 교체한다. 현재는 구조 검증용 최소 UI.
 */
@Composable
fun DetailPage(
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier,
) {
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val item = viewModel.item

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = item?.title ?: "표시할 항목이 없습니다",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = item?.description ?: "저장소에 없는 id 입니다 (deep-link 진입)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (item != null) {
            Button(onClick = viewModel::onClickFavorite) {
                Text(text = if (item.id in favoriteIds) "즐겨찾기 해제" else "즐겨찾기 추가")
            }
        }

        Button(onClick = viewModel::onClickBack) {
            Text(text = "뒤로")
        }
    }
}
