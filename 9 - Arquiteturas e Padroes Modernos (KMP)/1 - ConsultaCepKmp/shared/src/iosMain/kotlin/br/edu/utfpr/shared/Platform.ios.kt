package br.edu.utfpr.shared

import platform.UIKit.UIDevice

actual fun platform() = "iOS ${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}"