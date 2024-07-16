//
//  UIImage.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 16.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit

extension UIImage {
    func resizedAndCropped(to size: CGSize) -> UIImage? {
        let scale = max(size.width / self.size.width, size.height / self.size.height)
        let width = self.size.width * scale
        let height = self.size.height * scale
        let x = (size.width - width) / 2.0
        let y = (size.height - height) / 2.0
        let cropRect = CGRect(x: x, y: y, width: width, height: height)
        
        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
        self.draw(in: cropRect)
        let croppedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return croppedImage
    }
}
