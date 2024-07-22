//
//  UserProfile.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 15.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

struct UserProfile {
    let uid: String
    var firstName: String
    var lastName: String
    var email: String
    var surgeryDate: Date
    var mainMeals: Int = 3
    var betweenMeals: Int = 3
    var profileImageURL: String?
    var startWeight: Double
}
