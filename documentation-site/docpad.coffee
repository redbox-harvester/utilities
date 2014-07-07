# The DocPad Configuration File
# It is simply a CoffeeScript Object which is parsed by CSON
docpadConfig = {

	# =================================
	# Template Data
	# These are variables that will be accessible via our templates
	# To access one of these within our templates, refer to the FAQ: https://github.com/bevry/docpad/wiki/FAQ

	templateData:

		# Specify some site properties
		site:

			# Here are some old site urls that you would like to redirect from
			oldUrls: [
				'redbox-harvester.github.io/utilities'
			]

			# The default title of our website
			title: "ReDBox Harvester - Utilities"

			# The website description (for SEO)
			description: """
				Documentation for the ReDBox Harvester Utilities.
				"""

			# The website keywords (for SEO) separated by commas
			keywords: """
				ReDBox, Research, Data, Data Integration, Spring Integration, Spring, Groovy
				"""

			# The website author's name
			author: "Shilo Banihit"

			# The website author's email
			email: ""

			# Styles
			styles: [
				"/styles/twitter-bootstrap.css"
				"/styles/style.css"
				"/styles/stickyfooter.css"
			]

			# Scripts
			scripts: [
				"//cdnjs.cloudflare.com/ajax/libs/jquery/1.10.2/jquery.min.js"
				"//cdnjs.cloudflare.com/ajax/libs/modernizr/2.6.2/modernizr.min.js"
				"/vendor/twitter-bootstrap/dist/js/bootstrap.min.js"
				"/scripts/script.js"
			]
			# Custom properties
			projectName: "ReDBox Harvester"
			subProjectName: "Utilities"



		# -----------------------------
		# Helper Functions

		# Get the prepared site/document title
		# Often we would like to specify particular formatting to our page's title
		# we can apply that formatting here
		getPreparedTitle: ->
			# if we have a document title, then we should use that and suffix the site's title onto it
			if @document.title
				"#{@document.title} | #{@site.title}"
			# if our document does not have it's own title, then we should just use the site's title
			else
				@site.title

		# Get the prepared site/document description
		getPreparedDescription: ->
			# if we have a document description, then we should use that, otherwise use the site's description
			@document.description or @site.description

		# Get the prepared site/document keywords
		getPreparedKeywords: ->
			# Merge the document keywords with the site keywords
			@site.keywords.concat(@document.keywords or []).join(', ')


	# =================================
	# Collections
	# These are special collections that our website makes available to us

	collections:
		pages: (database) ->
			database.findAllLive({pageOrder: $exists: true}, [pageOrder:1,title:1])

		posts: (database) ->
			database.findAllLive({tags:$has:'post'}, [date:-1])


	# =================================
	# Plugins

	plugins:
		downloader:
			downloads: [
				{
					name: 'Twitter Bootstrap'
					path: 'src/files/vendor/twitter-bootstrap'
					url: 'https://codeload.github.com/twbs/bootstrap/tar.gz/master'
					tarExtractClean: true
				}
			]
		raw:
			raw:
				src: 'raw'


	# =================================
	# DocPad Events

	# Here we can define handlers for events that DocPad fires
	# You can find a full listing of events on the DocPad Wiki
	events:

		# Server Extend
		# Used to add our own custom routes to the server before the docpad routes are added
		serverExtend: (opts) ->
			# Extract the server from the options
			{server} = opts
			docpad = @docpad

			# As we are now running in an event,
			# ensure we are using the latest copy of the docpad configuraiton
			# and fetch our urls from it
			latestConfig = docpad.getConfig()
			oldUrls = latestConfig.templateData.site.oldUrls or []
			newUrl = latestConfig.templateData.site.url

			# Redirect any requests accessing one of our sites oldUrls to the new site url
			server.use (req,res,next) ->
				if req.headers.host in oldUrls
					res.redirect(newUrl+req.url, 301)
				else
					next()
	environments:
		development: # Default
			port: 9780
			templateData:
				site:
					url: "http://localhost:9780/"
					project: "utilities"
				parent:
					url: "http://localhost:9778/"
				client:
					url: "http://localhost:9779/"
		static: # The version hosted at GH pages
			templateData:
				site:
					url: "http://harvester-utilities-snapshot.redboxresearchdata.com.au/"
					project: "utilities"
				parent:
					url: "http://harvester-snapshot.redboxresearchdata.com.au/"
				client:
					url: "http://harvester-client-snapshot.redboxresearchdata.com.au/"
		release: # The released version hosted at wherever.
			templateData:
				site:
					url: "http://harvester-utilities-release.redboxresearchdata.com.au/"
					project: "utilities"
				parent:
					url: "http://harvester-release.redboxresearchdata.com.au/"
				client:
					url: "http://harvester-client-release.redboxresearchdata.com.au/"
}


# Export our DocPad Configuration
module.exports = docpadConfig
