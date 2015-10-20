package tw.tasker.babysitter.model;

import java.util.List;

public class GMapAddress {

    /**
     * results : [{"address_components":[{"long_name":"建國路一段31巷","short_name":"建國路一段31巷","types":["route"]},{"long_name":"埤頂里","short_name":"埤頂里","types":["administrative_area_level_4","political"]},{"long_name":"鳳山區","short_name":"鳳山區","types":["administrative_area_level_3","political"]},{"long_name":"高雄市","short_name":"高雄市","types":["administrative_area_level_1","political"]},{"long_name":"台灣","short_name":"TW","types":["country","political"]},{"long_name":"830","short_name":"830","types":["postal_code"]}],"formatted_address":"830台灣高雄市鳳山區建國路一段31巷","geometry":{"bounds":{"northeast":{"lat":22.635874,"lng":120.3791841},"southwest":{"lat":22.6326388,"lng":120.3772321}},"location":{"lat":22.6345875,"lng":120.3778885},"location_type":"GEOMETRIC_CENTER","viewport":{"northeast":{"lat":22.635874,"lng":120.3795570802915},"southwest":{"lat":22.6326388,"lng":120.3768591197085}}},"partial_match":true,"place_id":"ChIJoWGU0hobbjQRRNvOm32YynE","types":["route"]}]
     * status : OK
     */

    private String status;
    private List<ResultsEntity> results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResultsEntity> getResults() {
        return results;
    }

    public void setResults(List<ResultsEntity> results) {
        this.results = results;
    }

    public static class ResultsEntity {
        /**
         * address_components : [{"long_name":"建國路一段31巷","short_name":"建國路一段31巷","types":["route"]},{"long_name":"埤頂里","short_name":"埤頂里","types":["administrative_area_level_4","political"]},{"long_name":"鳳山區","short_name":"鳳山區","types":["administrative_area_level_3","political"]},{"long_name":"高雄市","short_name":"高雄市","types":["administrative_area_level_1","political"]},{"long_name":"台灣","short_name":"TW","types":["country","political"]},{"long_name":"830","short_name":"830","types":["postal_code"]}]
         * formatted_address : 830台灣高雄市鳳山區建國路一段31巷
         * geometry : {"bounds":{"northeast":{"lat":22.635874,"lng":120.3791841},"southwest":{"lat":22.6326388,"lng":120.3772321}},"location":{"lat":22.6345875,"lng":120.3778885},"location_type":"GEOMETRIC_CENTER","viewport":{"northeast":{"lat":22.635874,"lng":120.3795570802915},"southwest":{"lat":22.6326388,"lng":120.3768591197085}}}
         * partial_match : true
         * place_id : ChIJoWGU0hobbjQRRNvOm32YynE
         * types : ["route"]
         */

        private String formatted_address;
        private GeometryEntity geometry;
        private boolean partial_match;
        private String place_id;
        private List<AddressComponentsEntity> address_components;
        private List<String> types;

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public GeometryEntity getGeometry() {
            return geometry;
        }

        public void setGeometry(GeometryEntity geometry) {
            this.geometry = geometry;
        }

        public boolean getPartial_match() {
            return partial_match;
        }

        public void setPartial_match(boolean partial_match) {
            this.partial_match = partial_match;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public List<AddressComponentsEntity> getAddress_components() {
            return address_components;
        }

        public void setAddress_components(List<AddressComponentsEntity> address_components) {
            this.address_components = address_components;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public static class GeometryEntity {
            /**
             * bounds : {"northeast":{"lat":22.635874,"lng":120.3791841},"southwest":{"lat":22.6326388,"lng":120.3772321}}
             * location : {"lat":22.6345875,"lng":120.3778885}
             * location_type : GEOMETRIC_CENTER
             * viewport : {"northeast":{"lat":22.635874,"lng":120.3795570802915},"southwest":{"lat":22.6326388,"lng":120.3768591197085}}
             */

            private BoundsEntity bounds;
            private LocationEntity location;
            private String location_type;
            private ViewportEntity viewport;

            public BoundsEntity getBounds() {
                return bounds;
            }

            public void setBounds(BoundsEntity bounds) {
                this.bounds = bounds;
            }

            public LocationEntity getLocation() {
                return location;
            }

            public void setLocation(LocationEntity location) {
                this.location = location;
            }

            public String getLocation_type() {
                return location_type;
            }

            public void setLocation_type(String location_type) {
                this.location_type = location_type;
            }

            public ViewportEntity getViewport() {
                return viewport;
            }

            public void setViewport(ViewportEntity viewport) {
                this.viewport = viewport;
            }

            public static class BoundsEntity {
                /**
                 * northeast : {"lat":22.635874,"lng":120.3791841}
                 * southwest : {"lat":22.6326388,"lng":120.3772321}
                 */

                private NortheastEntity northeast;
                private SouthwestEntity southwest;

                public NortheastEntity getNortheast() {
                    return northeast;
                }

                public void setNortheast(NortheastEntity northeast) {
                    this.northeast = northeast;
                }

                public SouthwestEntity getSouthwest() {
                    return southwest;
                }

                public void setSouthwest(SouthwestEntity southwest) {
                    this.southwest = southwest;
                }

                public static class NortheastEntity {
                    /**
                     * lat : 22.635874
                     * lng : 120.3791841
                     */

                    private double lat;
                    private double lng;

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLng() {
                        return lng;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }
                }

                public static class SouthwestEntity {
                    /**
                     * lat : 22.6326388
                     * lng : 120.3772321
                     */

                    private double lat;
                    private double lng;

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLng() {
                        return lng;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }
                }
            }

            public static class LocationEntity {
                /**
                 * lat : 22.6345875
                 * lng : 120.3778885
                 */

                private double lat;
                private double lng;

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }
            }

            public static class ViewportEntity {
                /**
                 * northeast : {"lat":22.635874,"lng":120.3795570802915}
                 * southwest : {"lat":22.6326388,"lng":120.3768591197085}
                 */

                private NortheastEntity northeast;
                private SouthwestEntity southwest;

                public NortheastEntity getNortheast() {
                    return northeast;
                }

                public void setNortheast(NortheastEntity northeast) {
                    this.northeast = northeast;
                }

                public SouthwestEntity getSouthwest() {
                    return southwest;
                }

                public void setSouthwest(SouthwestEntity southwest) {
                    this.southwest = southwest;
                }

                public static class NortheastEntity {
                    /**
                     * lat : 22.635874
                     * lng : 120.3795570802915
                     */

                    private double lat;
                    private double lng;

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLng() {
                        return lng;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }
                }

                public static class SouthwestEntity {
                    /**
                     * lat : 22.6326388
                     * lng : 120.3768591197085
                     */

                    private double lat;
                    private double lng;

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLng() {
                        return lng;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }
                }
            }
        }

        public static class AddressComponentsEntity {
            /**
             * long_name : 建國路一段31巷
             * short_name : 建國路一段31巷
             * types : ["route"]
             */

            private String long_name;
            private String short_name;
            private List<String> types;

            public String getLong_name() {
                return long_name;
            }

            public void setLong_name(String long_name) {
                this.long_name = long_name;
            }

            public String getShort_name() {
                return short_name;
            }

            public void setShort_name(String short_name) {
                this.short_name = short_name;
            }

            public List<String> getTypes() {
                return types;
            }

            public void setTypes(List<String> types) {
                this.types = types;
            }
        }
    }
}
