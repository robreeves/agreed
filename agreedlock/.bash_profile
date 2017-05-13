function t() {
    curl -i -G -X GET http://$1/api/time
    printf "\n\n"
}

function tt() {
    t $1
    t $2
}