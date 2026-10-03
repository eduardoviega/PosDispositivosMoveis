//
//  GenderButton.swift
//  IMC
//
//  Created by Viasoft on 03/10/26.
//

import SwiftUI

struct GenderButton: View {
    let gender: Gender
    let action: () -> Void
    
    var body: some View {
        Button {
            action()
        } label: {
            Text(gender.name)
                .font(.title3)
                .fontWeight(.semibold)
                .fontDesign(.rounded)
        }
    }
}

#Preview {
    GenderButton(gender: .male) {
        print("Olá")
    }
}
