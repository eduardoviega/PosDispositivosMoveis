//
//  Gender.swift
//  IMC
//
//  Created by Viasoft on 03/10/26.
//

//Enum com valor padrão (.rawValue)
enum Gender: String {
    case male = "Male"
    case female = "Female"
    
    //Propriedade computada
    var name: String {
        switch self {
        case .male: 
            return "Homem"
        case .female:
            return "Mulher"
        }
    }
}
