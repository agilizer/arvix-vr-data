var MapsLeaflet = {

	basic: function () {
		var map = L.map('lm_1').setView([51.505, -0.09], 13);

		L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {
			maxZoom: 18,
			attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
				'Imagery © <a href="http://mapbox.com">Mapbox</a>',
			id: 'examples.map-i86knfo3'
		}).addTo(map);


		L.marker([51.5, -0.09]).addTo(map)
			.bindPopup("<b>Hello world!</b><br />I am a popup.").openPopup();

		L.circle([51.508, -0.11], 500, {
			color: 'red',
			fillColor: '#f03',
			fillOpacity: 0.5
		}).addTo(map).bindPopup("I am a circle.");

		L.polygon([
			[51.509, -0.08],
			[51.503, -0.06],
			[51.51, -0.047]
		]).addTo(map).bindPopup("I am a polygon.");


		var popup = L.popup();

		function onMapClick(e) {
			popup
				.setLatLng(e.latlng)
				.setContent("You clicked the map at " + e.latlng.toString())
				.openOn(map);
		}

		map.on('click', onMapClick);
	},

	customIcons: function () {
		var map = L.map('lm_2').setView([51.5, -0.09], 13);

		L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
			attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
		}).addTo(map);

		var LeafIcon = L.Icon.extend({
			options: {
				shadowUrl: 'assets/plugins/leaflet/images/leaf-shadow.png',
				iconSize:     [38, 95],
				shadowSize:   [50, 64],
				iconAnchor:   [22, 94],
				shadowAnchor: [4, 62],
				popupAnchor:  [-3, -76]
			}
		});

		var greenIcon = new LeafIcon({iconUrl: 'assets/plugins/leaflet/images/leaf-green.png'}),
			redIcon = new LeafIcon({iconUrl: 'assets/plugins/leaflet/images/leaf-red.png'}),
			orangeIcon = new LeafIcon({iconUrl: 'assets/plugins/leaflet/images/leaf-orange.png'});

		L.marker([51.5, -0.09], {icon: greenIcon}).bindPopup("I am a green leaf.").addTo(map);
		L.marker([51.495, -0.083], {icon: redIcon}).bindPopup("I am a red leaf.").addTo(map);
		L.marker([51.49, -0.1], {icon: orangeIcon}).bindPopup("I am an orange leaf.").addTo(map);
	},

	layerGroups: function () {
		var cities = new L.LayerGroup();

		L.marker([39.61, -105.02]).bindPopup('This is Littleton, CO.').addTo(cities);
		L.marker([39.74, -104.99]).bindPopup('This is Denver, CO.').addTo(cities);
		L.marker([39.73, -104.8]).bindPopup('This is Aurora, CO.').addTo(cities);
		L.marker([39.77, -105.23]).bindPopup('This is Golden, CO.').addTo(cities);


		var mbAttr = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
				'Imagery © <a href="http://mapbox.com">Mapbox</a>',
			mbUrl = 'https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png';

		var grayscale = L.tileLayer(mbUrl, {id: 'examples.map-20v6611k', attribution: mbAttr}),
				streets = L.tileLayer(mbUrl, {id: 'examples.map-i86knfo3',   attribution: mbAttr});

		var map = L.map('lm_3', {
			center: [39.73, -104.99],
			zoom: 10,
			layers: [grayscale, cities]
		});

		var baseLayers = {
			"Grayscale": grayscale,
			"Streets": streets
		};

		var overlays = {
			"Cities": cities
		};

		L.control.layers(baseLayers, overlays).addTo(map);
	},

	init: function () {
		this.basic();
		this.customIcons();
		this.layerGroups();
	}
}

