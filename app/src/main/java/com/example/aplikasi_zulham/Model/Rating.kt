package com.example.aplikasi_zulham.Model

class Rating {
    var Value : Int = 0;
    var IdUser : Int = 0 ;
    var IdTour : Int = 0;

    constructor(Value: Int, IdUser: Int, IdTour: Int) {
        this.Value = Value
        this.IdUser = IdUser
        this.IdTour = IdTour
    }

    constructor(Value: Int) {
        this.Value = Value
    }


}