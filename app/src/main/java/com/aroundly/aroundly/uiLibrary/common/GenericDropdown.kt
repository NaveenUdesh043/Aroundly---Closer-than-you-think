package com.aroundly.aroundly.uiLibrary.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.BgDark
import com.aroundly.aroundly.ui.theme.BgMid
import com.aroundly.aroundly.ui.theme.SurfaceWarm
import com.aroundly.aroundly.ui.theme.TextOnDarkMuted
import com.aroundly.aroundly.ui.theme.White
import com.aroundly.aroundly.ui.theme.White50
import com.aroundly.aroundly.ui.theme.White70
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericDropdown(
    label: String = "Select an option",
    options: List<String>,
    selectedOption: String = "",
    onOptionSelected: (String) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (selectedOption.isEmpty()) label else selectedOption,
            color = SurfaceWarm,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSheet = true }
                .border(1.dp, color = TextOnDarkMuted, RoundedCornerShape(8.dp))
                .padding(12.dp)
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = BgMid,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(White50)
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = SurfaceWarm,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.height(320.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(options) { option ->
                        val isSelected = option == selectedOption

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Amber.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    onOptionSelected(option)
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion {
                                        showSheet = false
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Amber else White50)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = option,
                                color = if (isSelected) Amber else White70,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold
                                else
                                    FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
