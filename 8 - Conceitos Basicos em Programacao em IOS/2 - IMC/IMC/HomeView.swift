//
//  HomeView.swift
//  IMC
//
//  Created by Viasoft on 03/10/26.
//

import SwiftUI

//Protocolo: Interface
//Conjunto de regras que definem como uma estrutura deve ser e se comportar

//Property Wrappers
//@Uppercased

struct HomeView: View {
    
    @State private var name: String = ""
    @State private var gender: Gender = .male
    @State private var height: Int = 175
    @State private var weight: Int = 70
    
    let showResult: (_ name: String, _ gender: Gender, _ imc: Double) -> Void
    
    var body: some View {
        VStack(spacing: 22) {
            title
            nameField
            mainImage
            genderSelectionButtons
            measureViews
            calculateIMCButton
            Spacer()
        }
        .frame(maxWidth: .infinity)
        .background(Color.accentColor.opacity(0.2))
        
    }
    
    // MARK: - Title
    var title: some View {
        Text("Calculadora de IMC")
            .font(.largeTitle)
            .fontDesign(.rounded)
            .fontWeight(.semibold)
    }
    
    // MARK: - Name Field
    var nameField: some View {
        HStack {
            Image(systemName: "person.fill")
            TextField("Digite seu nome", text: $name)
        }
        .padding(12)
        .background(.background)
        .clipShape(RoundedRectangle(cornerRadius: 32))
        .shadow(color: Color(white: 0.8), radius: 8)
        .padding(.horizontal)
    }

    // MARK: - Main Image
    var mainImage: some View {
        Image("\(gender.rawValue)3")
            .resizable()
            .scaledToFit()
            .frame(width: 180, height: 260)
    }
    
    // MARK: - Gender Selection Buttons
    var genderSelectionButtons: some View {
        HStack(spacing: 60) {
            GenderButton(gender: .male) {
                gender = .male
            }
            GenderButton(gender: .female) {
                gender = .female
            }
        }
    }

    // MARK: - Measure Views
    var measureViews: some View {
        VStack {
            MeasureView(image: "pencil.and.ruler", text: "Altura (cm)", value: $height)
            MeasureView(image: "scalemass.fill", text: "Peso (kg)", value: $weight)
        }
        .padding()
    }
    
    // MARK: - Calculate IMC Button
    var calculateIMCButton: some View {
        AppButton(text: "Calcular IMC") {
            let imc = Double(weight) / (Double(height*height)/10000)
            showResult(name, gender, imc)
        }
    }
}

#Preview {
    HomeView { name, gender, imc in
        
    }
}
