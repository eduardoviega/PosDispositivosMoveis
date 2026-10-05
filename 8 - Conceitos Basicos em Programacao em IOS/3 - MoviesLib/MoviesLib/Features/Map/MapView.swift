//
//  MapView.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import MapKit
import SwiftUI

struct MapView: View {
    @State private var searchString: String = ""
    @State private var locationManager = CLLocationManager()
    @State private var position: MapCameraPosition = .automatic //.userLocation(fallback: .automatic)
    @State private var selectedPOI: MKMapItem?
    @State private var pointsOfInterest: [MKMapItem] = []
    @State private var visibleRegion: MKCoordinateRegion?
    @State private var route: MKRoute?

    var body: some View {
        NavigationStack {
            Map(position: $position, selection: $selectedPOI) {
                UserAnnotation()

                ForEach(pointsOfInterest, id: \.self) { poi in
                    Marker(item: poi)
                }
                if let route {
                    MapPolyline(route)
                        .stroke(.purple, lineWidth: 6)
                        .mapOverlayLevel(level: .aboveRoads)
                }

            }
            .mapControls {
                MapUserLocationButton()
                MapCompass()
                MapScaleView()
            }
            .mapStyle(.standard(elevation: .realistic))
            .onMapCameraChange { context in
                visibleRegion = context.region
            }
        }
        .searchable(text: $searchString, prompt: "Digite o que está buscando")
        .onSubmit(of: .search, searchForPOI)
        .onAppear {
            locationManager.requestWhenInUseAuthorization()
            position = .userLocation(fallback: .automatic)
        }
        .onChange(of: selectedPOI) {
            getDirections()
        }
    }

    private func searchForPOI() {
        let request = MKLocalSearch.Request()
        request.naturalLanguageQuery = searchString
        request.region = visibleRegion ?? MKCoordinateRegion()

        let search = MKLocalSearch(request: request)
        Task {
            guard let response = try? await search.start() else { return }
            pointsOfInterest = response.mapItems
            position = .automatic
        }
    }

    private func getDirections() {
        route = nil
        guard let coordinate = locationManager.location?.coordinate,
              let selectedPOI else { return }

        let request = MKDirections.Request()
        request.source = MKMapItem(placemark: MKPlacemark(coordinate: coordinate))
        request.destination = selectedPOI

        let directions = MKDirections(request: request)
        Task {
            route = try? await directions.calculate().routes.first

            withAnimation {
                position = .camera(MapCamera(centerCoordinate: coordinate,
                                             distance: 400,
                                             heading: 0,
                                             pitch: 45))
            }

            print("Nome da rota:", route?.name ?? "")
            print("Distância em metros:", route?.distance ?? "")
            print("Duração em segundos:", route?.expectedTravelTime ?? "")

            for step in (route?.steps ?? []) {
                print(step.instructions)
            }
        }
    }
}

#Preview {
    MapView()
}
