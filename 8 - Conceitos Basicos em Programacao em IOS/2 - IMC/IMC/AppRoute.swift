//
//  AppRoute.swift
//  IMC
//
//  Created by Viasoft on 03/10/26.
//

//open, public, internal, fileprivate, private

enum AppRoute: Hashable {
    case result(name: String, gender: Gender, imc: Double)
    case list
}
