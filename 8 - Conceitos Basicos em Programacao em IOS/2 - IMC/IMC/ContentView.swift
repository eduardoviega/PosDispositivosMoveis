//
//  ContentView.swift
//  IMC
//
//  Created by Viasoft on 03/10/26.
//

import SwiftUI

struct ContentView: View {
    @State private var navigationPath = NavigationPath()
    
    var body: some View {
        NavigationStack(path: $navigationPath) {
            HomeView { name, gender, imc in
                navigationPath.append(
                    AppRoute.result(
                        name: name,
                        gender: gender,
                        imc: imc)
                )
            }
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                    case .result(let name, let gender, let imc):
                        ResultView(
                            name: name,
                            gender: gender,
                            imc: imc,
                            navigationPath: $navigationPath
                        )
                    case .list:
                        ListView()
                    }
                }
        }

    }
}

#Preview {
    ContentView()
}
