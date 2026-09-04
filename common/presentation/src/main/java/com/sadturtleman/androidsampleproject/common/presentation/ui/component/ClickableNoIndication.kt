package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

/**
 * 리플 없는 클릭 영역.
 *
 * 검색 바 안의 지우기 버튼처럼, 컨테이너 안에 얹히는 작은 아이콘은
 * 원본 디자인에 눌림 표현이 없어 리플을 끈다.
 */
@Composable
internal fun Modifier.clickableNoIndication(
    enabled: Boolean = true,
    role: Role = Role.Button,
    onClick: () -> Unit,
): Modifier = clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    enabled = enabled,
    role = role,
    onClick = onClick,
)
