//
//  MoviePoster.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import SwiftUI

struct MoviePoster: View {
    let posterPath: String
    
    var body: some View {
        if let url = URL(string: posterPath) {
            AsyncImage(url: url) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFill()
                default:
                    placeholder
                }
            }
        } else {
            placeholder
        }
    }
    
    private var placeholder: some View {
        Image(systemName: "movieclapper")
            .resizable()
            .scaledToFit()
            .foregroundStyle(Color.gray.opacity(0.3))
    }
}

#Preview {
    MoviePoster(posterPath: "https://acdn-us.mitiendanube.com/stores/004/687/740/products/pos-00903-641b31af291835791717181354202331-640-0.webp")
}
