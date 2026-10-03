//
//  IMCStorage.swift
//  IMC
//
//  Created by Viasoft on 03/10/26.
//

import SwiftUI


struct IMCData: Codable, Identifiable {
    let id: UUID
    let name: String
    let gender: String
    let imc: Double
    
    init(name: String, gender: String, imc: Double) {
        self.id = UUID()
        self.name = name
        self.gender = gender
        self.imc = imc
    }
}

class IMCStorage {
    @AppStorage("lista_imc") private var imcList: Data = Data()
    
    var items: [IMCData] {
        get {
            return (try? JSONDecoder().decode([IMCData].self, from: imcList)) ?? []
        }
        set {
            imcList = (try? JSONEncoder().encode(newValue)) ?? Data()
        }
    }
}

