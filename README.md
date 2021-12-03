Task ID: Stock Trading App



This is a Stock prices watching app

There are few important things to keep in mind before using this app:

i) the api used has a limit of using only 8 credits per minute,1 symbol being equal to 1 credit so whenever you see a prompt to "kindly refresh the app" 
   then all you have to do is swipe down to refresh the app exactly after 1 min. If this doesn't help then kill the app and try exactly after 1 min
   
ii) Let MainActivity load fully since it retrieves the email id of the active user and this task is asynchronous you have to give some time to load.
    Please do not jump MainActivity before it is loaded.
    
iii) In the search section you can search any stock but for best results USE THE STOCK SYMBOL as shown in the demo video. 
    
This Gdrive url has the video of the working app:

https://drive.google.com/folderview?id=1ka_YkKVZxgo4QC_wPEgbAJ8Ef7Zzlw64

Here are the dependencies used in the app


dependencies {

    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.1'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.google.firebase:firebase-database:20.0.2'
    implementation 'com.google.firebase:firebase-auth:21.0.1'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
    implementation 'com.squareup.okhttp3:okhttp:3.6.0'
    implementation 'com.android.volley:volley:1.2.1'
    implementation "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"

    implementation platform('com.google.firebase:firebase-bom:29.0.0')


}
