//
//  MovieAPIService.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import Foundation

enum MovieAPIError: Error {
    case missingMovieID
    case decoding
    case invalidURL
    case unknown
    case invalidResponse
    case httpError(statusCode: Int)
    
    var errorDescription: String {
        switch self {
        case .missingMovieID:
            return "Filme não possui ID"
        case .unknown:
            return "Erro desconhecido"
        case .invalidURL:
            return "URL inválida"
        case .invalidResponse:
            return "Servidor devolveu uma resposta inválida. Tente mais tarde!"
        case .httpError(statusCode: let statusCode):
            return "Erro ao acessar o serviço. Código \(statusCode)"
        case .decoding:
            return "Erro de decoding"
        }
    }
}

class MovieAPIService {
    private let basePath = "https://movies-api.eric-brito.workers.dev/movies"
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()
    
    private let configuration: URLSessionConfiguration = {
        let configuration = URLSessionConfiguration.default
        configuration.httpAdditionalHeaders = ["Content-type": "application/json"]
        configuration.timeoutIntervalForRequest = 10
        configuration.allowsCellularAccess = true
        configuration.httpMaximumConnectionsPerHost = 5
        return configuration
    }()
    
//    private var session: URLSession = .shared
    private lazy var session: URLSession = URLSession(configuration: configuration)
    
    func getMovies() async throws -> [Movie] {
        let data = try await performRequest()
        do {
            return try decoder.decode([Movie].self, from: data)
        } catch {
            throw MovieAPIError.decoding
        }
    }
    
    func deleteMovie(_ movie: Movie) async throws {
        guard let id = movie.id else { throw MovieAPIError.missingMovieID }
        try await performRequest(endpoint: "/\(id)", method: "DELETE")
    }
    
    func createMovie(_ movie: Movie) async throws {
        let movieBody = try encoder.encode(movie)
        try await performRequest(method: "POST", body: movieBody)
    }
    
    func updateMovie(_ movie: Movie) async throws {
        guard let id = movie.id else { throw MovieAPIError.missingMovieID }
        let movieBody = try encoder.encode(movie)
        try await performRequest(endpoint: "/\(id)", method: "PUT", body: movieBody)
    }
    
    //DRY (Don't Repeat Yourself)
    @discardableResult
    private func performRequest(
        endpoint: String = "",
        method: String = "GET",
        body: Data? = nil
    ) async throws -> Data {
        
        guard let url = URL(string: basePath + endpoint) else {
            throw MovieAPIError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = method
        request.httpBody = body
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw MovieAPIError.invalidResponse
        }
        
        guard 200...299 ~= httpResponse.statusCode else {
            throw MovieAPIError.httpError(statusCode: httpResponse.statusCode)
        }
        
        return data
    }
}
