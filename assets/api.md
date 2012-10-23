This document refer to all the available api end points that can be accessed using a valid auth token. Please refer to Badge.md for more details on 'badging engine'.

Note: Most of the api calls require client to pass along auth_token with every requests, except actions like new/create. Or else you will be get a '302/redirect to home page'

## Check in - Requires auth_token

	Create a check in
	------------------
		Endpoint: /api/check_in
		Method: POST

		Params:
			track_title
			itunes_track_id
			itunes_artist_id
			youtube_video_id
			video_thumbnail_url
			itunes_preview_url
			itunes_download_url
			video_title
			artist
			live
			latitude
			longitude
			place
			files
			local_time

		Returns:
			As JSON - {
				"success":true,
				"valid_play":true,
				"manager_distance":10,
				"current_manager":null,
				"current_manager_profile_image":null,
				"tiny_url":"7njYkd",
				"message":"Sweet! This is your 1st Linkin Park play and 2nd play overall",
				"ousted_manager":false,
				"snapshot":{
				    "small":"http://nwplyng.com/snapshots/small/missing.png",
				    "original":"http://nwplyng.com/snapshots/original/missing.png"
				    }
				}
			}

		See also: (User -> Getting new records assigned to a user)

	Get all check ins
	------------------
		Endpoint: /api/checkin.json
		Method: GET

		Params:
			user_id [OPTIONAL] 	If given returns details of that user
			page 		[OPTIONAL]- The start page, default is 1
			rpp 		[OPTIONAL]	Response per page, default is 50
            since_id   [OPTIONAL]   checkins since this id
            max_id      [OPTIONAL]   checkins less than this id
		Returns:
			As paginated JSON - {
				"count": 5,
			  "page": 1,
			  "per_page": "5",
			  "pages_count": 2,
			  "results": [
		    	{
						title    						check_in.title
						comment							check_in.comment
						live								check_in.live
						created_at					check_in.created_at
						snapshot_file_name  check_in.snapshot_url({host: request.host_with_port})
						trackName           check_in.track.title
						place               check_in.loaction
						artistName	        check_in.artist.name
						thumbnailUrl		    check_in.video_thumbnail_url
					  previewUrl		      check_in.itunes_preview_url
					  youtube_id          check_in.youtube_id
					  public_url					check_in.public_url
					  profile_image_url  user_profile_image
					   user_id                     user_id
					   career_progression users_career_rank_nmae
					   check_in_id        checkin.id
					}
					...
				]
			}

## User 

	Creating a user
	---------------

		Endpoint: /api/users/
		Method: POST

		Params:
			name (required)
			email 
			birthday
			facebook_id
			twitter_id
			foursquare_id
			fullname

			(all except name are optional)

		Returns:
			As JSON - {
				success => true/false
				response => "Success" or error description
			  auth_token => string (if :success == true)
			}


	Look up a Username
	------------------
		Endpoint: /api/users/user_name_availablity
		Method: get
        Params:
			name


		Returns:
			As JSON
			Success - {:availability => "yes"}
			Failure - {:availability => "no"}
  	Look up a User
	------------------
		Endpoint: /api/users/look_up
		Method: POST

		Signing algorithm: SHA1
		Creating digest:
			api_secret_key: "3485879cdd7b6656151473f5982d8abfc137cbb0"
			step 1:
			  hash_string = "<unix_time-stamp><api_secret_key><social_id*><email>"
			  *[social_id can be facebook_id, twitter_id or foursquare_id]
			step 2:
				digest = Apply SHA1 on hash_string

		Params:
			facebook_id (or) twitter_id (or) foursquare_id
			timestamp(unix_time-stamp used for signing)
			email
			digest

		Returns:
			As JSON
			Success - {
				success => true,
				response => "Success",
				user => {
					"auth_token"=>"token0",
					"birthday"=>"valid_birthday",
					"email"=>"valid_email_id",
					"facebook_id"=>"valid_facebook_id if present",
					"foursquare_id"=>"valid_foursquare_id if present",
					"fullname"=>"full_name",
					"name"=>"abc0",
					"twitter_id"=>"valid_twitter_id if present",
					"updated_at"=>"2012-06-11T05:20:54Z"
				}
			}

			Error -{
				success => false,
				response => "Error, access forbidden"
			}


	Updating social_id for a user
	-----------------------------
		Endpoint: /api/users/add_social_key
		Method: POST

		Params:
			facebook_id (or) twitter_id (or) foursquare_id
			auth_token

		Returns:
			As JSON
			Success - {
				"success":true,
				"response":"Success",
				"auth_token":"token0",
				"user_id":1,
				"name":"abc0"
			}

			Error - error message as returned by the user model


	Getting user profile data
	--------------------------

		Endpoint: /api/users/profile_data
		Method: GET

		Params:
			user_id - [OPTIONAL] if given returns details of that user

		Returns:
			As JSON - {
				"name": "vishnu", 
				"twitter_handle": "abc",
				"plays":10,
				"records":0,
				"followers":0,
				"following":1,
				"profile_image": <image_url>
				"created_at": date,
				"manages": 1,
				"career_progression": "airguitarist",
				user_id: 12,
				following_status: "following",
				fullname: "vishnu r"
				"last_played": { 
            "artist": "last_played_artist",
            "track": "last_played_track"
            "title": last_checkin_title,
            "comment": last_checkin_comment,
            "live": last_checkin_is_live,
            "created_at": last_checkin_created_at,
            "snapshot_file_name": last_checkin_snapshot_url,
            "trackName": last_checkin_track_title,
            "place"  : last_checkin_loaction,
            "artistName": last_checkin_artist_name,
            "itunes_download_url": last_checkin_itunes_download_url,
            "thumbnailUrl": last_checkin_video_thumbnail_url,
            "previewUrl": last_checkin_itunes_preview_url,
            "youtube_id" : last_checkin_youtube_id,
            "public_url": last_checkin_public_url,
          }

			}

	Getting records assigned to a user
	-------------------------------------

		Endpoint: /api/users/records
		Method: GET

		Params:
			filter (OPTIONAL) - if passed will return only the newly awarded records.

		Returns:
			As JSON list - [
					{ 
					  "name":"iMusic",
					  "description":"Woohoo! You unlocked iMusic Record for using the iPhone."
					},
					{
					   "name":"Single",
					   "description":"Your 1st play! Hope you're not a one hit wonder."},
					}
				]

	Getting rank/career_level of a user
	-------------------------------------

	Endpoint: /api/users/career_progression
	Method: GET

	Returns: 
		As JSON
		{
			"current_rank":{
				"name":"AirGuitarist","description":"Don't lose hope. Even Hendrix started off with air guitaring.","level":1,"just_promoted":true
			},
			"next_rank":{
				"name":"Street Performer","fans":2,"plays":21
			}
		}

	Searching user
	-------------------------------------

	Endpoint: /api/users/search
	Method: GET

	Params:
		name - search parameter
		page 	[OPTIONAL]- The start page, default is 1
		rpp 	[OPTIONAL]	Response per page, default is 50


	Returns: 
		As JSON
		[
			{"id"=>1, "name"=>"abc0", "follow_status"=>"false","profile_image"=>"","full_name"=>"fullnameabc0"},
			{"id"=>2, "name"=>"abc1", "follow_status"=>"true"}, 
			{"id"=>3, "name"=>"abc2", "follow_status"=>"true"}, 
			{"id"=>4, "name"=>"abc3", "follow_status"=>"false"}, 
			{"id"=>5, "name"=>"abc4", "follow_status"=>"false"}
		]

	Get all followers 
	-----------------

	Endpoint: /api/users/followers.json
	Method: GET

	Params:
			user_id [OPTIONAL] 	If given returns details of that user
			page 		[OPTIONAL]- The start page, default is 1
			rpp 		[OPTIONAL]	Response per page, default is 50

	Returns:
		"count": 1,
		  "page": 1,
		  "per_page": "5",
		  "pages_count": 1,
		  "master_name": "abc0",
		  "results": [
		    {
		      "created_at": "2012-05-04T15:18:01Z",
		      "name": "abc2",
		      "fullname": null,
		      "follower_id": 3,
		      "follow_status": true,
		      "profile_image": null,
		      "is_current_user": false
		    }
		  ]

	Get all following users 
	-----------------

	Endpoint: /api/users/following.json
	Method: GET

	Params:
			user_id [OPTIONAL] 	If given returns details of that user
			page 		[OPTIONAL]- The start page, default is 1
			rpp 		[OPTIONAL]	Response per page, default is 50

	Returns:
		"count": 1,
		  "page": 1,
		  "per_page": "5",
		  "pages_count": 1,
		  "master_name": "abc0",
		  "results": [
		    {
		      "created_at": "2012-05-04T15:18:01Z",
		      "name": "abc2",
		      "fullname": null,
		      "following_id": 3,
		      "follow_status": true,
		      "profile_image": null,
		      "is_current_user": false
		    }
		  ]

  Get all Artist Manages
    -----------------

    Endpoint: /api/manages
    Method: GET



    Returns:
        [
            {
              "name": artist_name,
              "itunes_id": itunes_artist_id,
            }
          ]


## Subscriptions - Requires auth_token

	Following a user
	----------------

		Endpoint: /api/subscriptions/
		Method: POST

		Params:
			target_id

		Returns:
			As JSON -
				{   :status => "Ok", 
	                :response => "Success", 
	                :subscription_id => subscription.id
	            }


	Stop following a user
	----------------

		Endpoint: /api/subscriptions/
		Method: DELETE

		Params:
			target_id

		Returns:
			As JSON -
				{   :status => "Ok", 
	                :response => "Success", 
	                :subscription_id => subscription.id
	            }


 
## Notifications - Requires auth_token

	Get all unread notifications
	----------------------------

		Endpoint: /api/notification.json
		Method: GET

		Params:
			none

		Returns:
			As paginated JSON - {
				"count": 1,
				"page": 1,
				"per_page": "10",
				"pages_count": 1,
				"results": [
				  {
				    "message": "someone followed you",
				    "created_at": "2012-05-01T17:07:33Z",
				    "status": false,
				    "type": "follow",
				    "action_user_id": 3,
				    "profile_image_url": null
				  }
				  ..
	  		]
	  	}		

## Preferences - Requires auth_token

	Get preferences of a user
	----------------------------

		Endpoint: /api/user_preferences
		Method: GET

		Params:
			none

		Returns:
			{
				"career_progression_email"=>true, 
				"following_email"=>true, 
				"manager_email"=>false, 
				"manager_ousted_email"=>false,
				"record_unlock_email"=>true, 
			} 

	Update preferences of a user
	----------------------------

		Endpoint: /api/user_preferences
		Method: POST

		Params:
			manager_email 
			manager_ousted_email
			record_unlock_email
			career_progression_email
			following_email

		Returns:
			status - True or False


## Feed - Requires auth_token

	Getting feeds for a user
	----------------------------

	Endpoint: /api/feed.json
	Method: GET

	Params:
		page 		[OPTIONAL]- The start page, default is 1
		rpp 		[OPTIONAL]	Response per page, default is 50
		since_id    [OPTIONAL]	Response since this id,
		max_id      [OPTIONAL]	Response before this id,

	Returns:
		As paginated JSON - {
					"count": 5,
				  "page": 1,
				  "per_page": "5",
				  "pages_count": 2,
				  "results": [
						{
							"title": "Gym Class Heroes - The Fighter ft. Ryan Tedder",
				      "comment": "Awsome comment..",
				      "youtube_id": "z7mzEbP0CS8",
				      "live": null,
				      "snapshot_file_name": "http://localhost:3000/snapshots/original/missing.png",
				      "user_name": "abc0",
				      "created_at": "2012-05-04T15:21:35Z",
				      "place": "mock location",
				      "artist_name": "Gym Class Heroes",
				      "thumbnail_url": "http://../..",
				      "itunes_preview_url": ""http://../..",
				      "itunes_download_url": ""
				      "profile_image_url": "http://api.twitter.com/1/users/profile_image/13.png",
				      "track_name": "The Fighter ft. Ryan Tedder",
				      "career_progression": "AirGuitarist",
				      "user_id": 1,
				      "unique_id": "0ETy4A"
				      "feed_id": 312
						}
					]
				}
