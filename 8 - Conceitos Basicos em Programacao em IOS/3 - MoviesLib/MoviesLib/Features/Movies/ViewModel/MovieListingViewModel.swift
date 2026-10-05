//
//  MovieListingViewModel.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import Foundation
import Observation

@Observable
class MovieListingViewModel {
    private(set) var movies: [Movie] = []
    private let service = MovieAPIService()
    var errorMessage: String?
    var hasError: Bool {
        get { errorMessage != nil }
        set { errorMessage = nil }
    }
    
    var isLoading: Bool = false
    
    func loadMovies() async {
        isLoading = true
        errorMessage = nil
        
        defer { isLoading = false }
        
        do {
            movies = try await service.getMovies()
        } catch {
            if let error = error as? MovieAPIError {
                errorMessage = error.errorDescription
            }
        }
    }
    
    func deleteMovie(at indexes: IndexSet) async {
        errorMessage = nil
        
        for index in indexes {
            do {
                try await service.deleteMovie(movies[index])
                movies.remove(at: index)
            } catch {
                if let error = error as? MovieAPIError {
                    errorMessage = error.errorDescription
                }
            }
        }
    }
}

