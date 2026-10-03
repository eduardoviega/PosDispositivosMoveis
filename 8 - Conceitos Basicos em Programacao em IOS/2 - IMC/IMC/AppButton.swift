//
//  AppButton.swift
//  IMC
//
//  Created by Viasoft on 03/10/26.
//

import SwiftUI

struct AppButton: View {
    let text: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.title3)
                .fontWeight(.semibold)
                .fontDesign(.rounded)
                .frame(maxWidth: .infinity, minHeight: 40)
        }
        .buttonStyle(.borderedProminent)
        .padding(.horizontal)
    }
}

#Preview {
    AppButton(text: "Calcular IMC") {
        
    }
}
