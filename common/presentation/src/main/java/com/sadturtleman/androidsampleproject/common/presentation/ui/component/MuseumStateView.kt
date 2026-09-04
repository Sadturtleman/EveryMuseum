package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/*
 * 로딩 · 에러 · 빈 상태 뷰.
 *
 * Figma 원본에는 이 세 화면이 없다. 실제 앱에서는 반드시 필요하고
 * 화면마다 따로 만들면 표현이 갈리므로, 디자인 토큰만 사용해 여기에 한 벌로 둔다.
 * 시안이 나오면 이 파일만 교체하면 모든 화면에 반영된다.
 */

/** 데이터를 기다리는 동안 표시한다. */
@Composable
fun MuseumLoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MuseumTheme.colors.iconBrand)
    }
}

/**
 * 조회에 실패했을 때 표시한다.
 *
 * @param onRetry null 이면 재시도 버튼을 그리지 않는다.
 */
@Composable
fun MuseumErrorView(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    MuseumMessageView(
        icon = MuseumIcons.Close,
        title = "불러오지 못했습니다",
        description = message,
        modifier = modifier,
        actionLabel = "다시 시도".takeIf { onRetry != null },
        onAction = onRetry,
    )
}

/** 조회는 성공했지만 결과가 없을 때 표시한다. */
@Composable
fun MuseumEmptyView(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    @DrawableRes icon: Int = MuseumIcons.Search,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    MuseumMessageView(
        icon = icon,
        title = title,
        description = description,
        modifier = modifier,
        actionLabel = actionLabel,
        onAction = onAction,
    )
}

@Composable
private fun MuseumMessageView(
    @DrawableRes icon: Int,
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    // fillMaxWidth 만 잡는다. 전체 화면으로 쓸 때는 호출부가 가운데 정렬 Box 로 감싸고,
    // LazyColumn 안에 인라인으로 넣을 때는 그대로 쓰면 된다. (fillMaxSize 면 스크롤 안에서 깨진다)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MuseumTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        MuseumIcon(
            id = icon,
            contentDescription = null,
            tint = MuseumTheme.colors.iconSecondary,
            size = 40.dp,
        )
        Text(
            text = title,
            modifier = Modifier.padding(top = MuseumTheme.spacing.lg),
            style = MuseumTheme.typography.headline,
            color = MuseumTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        if (description != null) {
            Text(
                text = description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MuseumTheme.spacing.sm),
                style = MuseumTheme.typography.bodyS,
                color = MuseumTheme.colors.textTertiary,
                textAlign = TextAlign.Center,
            )
        }
        if (actionLabel != null && onAction != null) {
            MuseumButton(
                label = actionLabel,
                onClick = onAction,
                modifier = Modifier.padding(top = MuseumTheme.spacing.xxl),
                style = MuseumButtonStyle.Secondary,
                size = MuseumButtonSize.M,
            )
        }
    }
}

@Preview(name = "상태 뷰 · Light", showBackground = true, backgroundColor = 0xFFF7F4EE, heightDp = 720)
@Preview(
    name = "상태 뷰 · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    heightDp = 720,
)
@Composable
private fun MuseumStateViewPreview() {
    EveryMuseumTheme {
        Column {
            Box(modifier = Modifier.weight(1f)) { MuseumLoadingView() }
            Box(modifier = Modifier.weight(1f)) {
                MuseumErrorView(message = "네트워크에 연결할 수 없습니다.", onRetry = {})
            }
            Box(modifier = Modifier.weight(1f)) {
                MuseumEmptyView(
                    title = "검색 결과가 없습니다",
                    description = "다른 검색어로 다시 시도해보세요.",
                )
            }
        }
    }
}
