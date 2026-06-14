package com.assistant.phoenix

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class PhoenixAccessibilityService : AccessibilityService() {
    private val TAG = "Phoenix_Service"

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Phoenix Accessibility Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootNode: AccessibilityNodeInfo = rootInActiveWindow ?: return

        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) {
            val packageName = event.packageName?.toString() ?: "Unknown application"
            Log.d(TAG, "---- Active application: $packageName ----")
            printNodeTree(rootNode, 0)
        }

    }

    override fun onInterrupt() {
        Log.d(TAG, "Phoenix Accessibility Service has been terminated via interruption")
    }


    fun clickAt(x: Float, y: Float) {
        val path = android.graphics.Path().apply { moveTo(x, y) }
        val gestureBuilder = android.accessibilityservice.GestureDescription.Builder()
        gestureBuilder.addStroke(
            android.accessibilityservice.GestureDescription.StrokeDescription(path, 0, 50)
        )

        dispatchGesture(gestureBuilder.build(), null, null)
        Log.d(TAG, "Universal click performed: X=$x, Y=$y")
    }

    fun performGlobalActionWrapper(actionId: Int) {
        performGlobalAction(actionId)
    }


    private fun printNodeTree(node: AccessibilityNodeInfo?, depth: Int) {
        if (node == null) return

        val indent = "  ".repeat(depth * 2)
        val nodeText = node.text?.toString() ?: ""
        val viewId = node.viewIdResourceName?.toString() ?: ""

        if (nodeText.isNotEmpty() && viewId.isNotEmpty()) {
            Log.d(TAG, "$indent[ELEMENT] ID: $viewId  | Text: $nodeText  | Clicked: ${node.isClickable}")
        }

        for (i in 0 until node.childCount) {
            printNodeTree(node.getChild(i), depth + 1)
        }
    }
}