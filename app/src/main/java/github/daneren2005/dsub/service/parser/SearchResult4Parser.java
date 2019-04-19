/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package github.daneren2005.dsub.service.parser;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import github.daneren2005.dsub.domain.Artist;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.domain.Playlist;
import github.daneren2005.dsub.domain.SearchResult;
import github.daneren2005.dsub.util.ProgressListener;

/**
 * @author Sindre Mehus
 */
public class SearchResult4Parser extends MusicDirectoryEntryParser {

    public SearchResult4Parser(Context context, int instance) {
		super(context, instance);
	}

    public SearchResult parse(Reader reader, ProgressListener progressListener) throws Exception {
        init(reader);

        List<Artist> artists = new ArrayList<Artist>();
        List<MusicDirectory.Entry> albums = new ArrayList<MusicDirectory.Entry>();
        List<MusicDirectory.Entry> songs = new ArrayList<MusicDirectory.Entry>();
        List<Playlist> playlists = new ArrayList<Playlist>();

        int eventType;
        do {
            eventType = nextParseEvent();
            if (eventType == XmlPullParser.START_TAG) {
                String name = getElementName();
                if ("artist".equals(name)) {
                    Artist artist = new Artist();
                    artist.setId(get("id"));
                    artist.setName(get("name"));
                    artists.add(artist);
                } else if ("album".equals(name)) {
					MusicDirectory.Entry entry = parseEntry("");
					entry.setDirectory(true);
                    albums.add(entry);
                } else if ("playlist".equals(name)) {
                    String id = get("id");
                    String pls_name = get("name");
                    String owner = get("owner");
                    String comment = get("comment");
                    String songCount = get("songCount");
                    String pub = get("public");
                    String created = get("created");
                    String changed = get("changed");
                    Integer duration = getInteger("duration");
                    playlists.add(new Playlist(id, pls_name, owner, comment, songCount, pub, created, changed, duration));
                } else if ("song".equals(name)) {
                    songs.add(parseEntry(""));
                } else if ("error".equals(name)) {
                    handleError();
                }
            }
        } while (eventType != XmlPullParser.END_DOCUMENT);

        validate();

        return new SearchResult(artists, albums, songs, playlists);
    }

}