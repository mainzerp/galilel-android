package galilel.org.galilelwallet.ui.splash_activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;
import android.view.ViewGroup;

import galilel.org.galilelwallet.GalilelApplication;
import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.ui.start_activity.StartActivity;
import galilel.org.galilelwallet.ui.wallet_activity.WalletActivity;

public class SplashActivity extends AppCompatActivity {
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                // get your video's width and height.
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                // get videoview's current width and height.
                int videoViewWidth = videoView.getWidth();
                int videoViewHeight = videoView.getHeight();

                // get scaling factor.
                float xScale = (float) videoViewWidth / videoWidth;
                float yScale = (float) videoViewHeight / videoHeight;

                // center crop the video.
                float scale = Math.max(xScale, yScale);

                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;

                // set the new size for the videoview based on the dimensions of the video.
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();

                layoutParams.width = (int)scaledWidth;
                layoutParams.height = (int)scaledHeight;

                videoView.setLayoutParams(layoutParams);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent;

                // jump to your next activity or main activity.
                if (GalilelApplication.getInstance().getAppConf().isAppInit())
                    intent = new Intent(getApplicationContext(), WalletActivity.class);
                else
                    intent = new Intent(getApplicationContext(), StartActivity.class);

                // start activity.
                startActivity(intent);

                // finish activity.
                finish();
            }
        });

        videoView.start();
    }
}
